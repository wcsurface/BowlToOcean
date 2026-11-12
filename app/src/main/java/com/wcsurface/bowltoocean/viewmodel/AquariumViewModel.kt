package com.wcsurface.bowltoocean.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wcsurface.bowltoocean.data.AquariumRepository
import com.wcsurface.bowltoocean.data.AquariumSaveData
import com.wcsurface.bowltoocean.data.RemoteGameConfig
import com.wcsurface.bowltoocean.engine.AquariumEngine
import com.wcsurface.bowltoocean.model.FishInstance
import com.wcsurface.bowltoocean.model.MaintenanceAction
import com.wcsurface.bowltoocean.model.TankTier
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AquariumUiState(
    val carePoints: Long = 1250L,
    val diamonds: Long = 25L,
    val playerLevel: Int = 3,
    val playerXp: Int = 180,
    val xpToNextLevel: Int = 300,
    val activeTankTier: TankTier = TankTier.BOWL,
    val unlockedTanks: List<TankTier> = listOf(TankTier.BOWL),
    val fishInstances: List<FishInstance> = emptyList(),
    val bioLoadBalance: Float = 85f,
    val bioLoadMultiplier: Float = 2.0f,
    val isMurkyWater: Boolean = false,
    val isSparklingWater: Boolean = true,
    val isHudVisible: Boolean = true,
    val isMaintainDrawerOpen: Boolean = false,
    val isShopSheetOpen: Boolean = false,
    val isSettingsOpen: Boolean = false,
    val dailyBonusClaimable: Boolean = true,
    val remoteNotice: String? = null
)

class AquariumViewModel(application: Application) : AndroidViewModel(application) {

    val repository = AquariumRepository(application.applicationContext)
    val engine = AquariumEngine(initialTankTier = TankTier.BOWL)

    private val _uiState = MutableStateFlow(AquariumUiState())
    val uiState: StateFlow<AquariumUiState> = _uiState.asStateFlow()

    private var remoteConfig = RemoteGameConfig()
    private var syncJob: Job? = null

    init {
        viewModelScope.launch {
            // 1. Load DataStore saved state locally for 0ms startup
            val localSave = repository.saveDataFlow.first()
            applySaveDataToEngine(localSave)

            // 2. Fetch live remote config from Railway URL
            val config = repository.fetchRemoteConfig()
            remoteConfig = config
            _uiState.update { it.copy(remoteNotice = config.closedTestingNotice) }

            // 3. Periodic state observer updating UI state and bio-load loop
            launch {
                while (true) {
                    delay(200)
                    updateUiStateFromEngine()
                }
            }
        }
    }

    private fun applySaveDataToEngine(saveData: AquariumSaveData) {
        val tier = try {
            TankTier.valueOf(saveData.activeTankTier)
        } catch (_: Exception) {
            TankTier.BOWL
        }
        engine.carePoints = saveData.carePoints
        engine.diamonds = saveData.diamonds
        engine.playerLevel = saveData.playerLevel
        engine.playerXp = saveData.playerXp
        engine.bioLoadBalance = saveData.bioLoadBalance
        engine.setTankTier(tier)

        engine.unlockedTanks.clear()
        for (tierName in saveData.unlockedTanks) {
            try {
                val t = TankTier.valueOf(tierName)
                if (!engine.unlockedTanks.contains(t)) {
                    engine.unlockedTanks.add(t)
                }
            } catch (_: Exception) {
            }
        }
        updateUiStateFromEngine()
    }

    private fun updateUiStateFromEngine() {
        val bioLoad = engine.bioLoadBalance
        val multiplier = when {
            bioLoad > 80f -> 2.0f
            bioLoad < 30f -> 0.5f
            else -> 1.0f
        }

        _uiState.update { current ->
            current.copy(
                carePoints = engine.carePoints,
                diamonds = engine.diamonds,
                playerLevel = engine.playerLevel,
                playerXp = engine.playerXp,
                xpToNextLevel = engine.xpToNextLevel,
                activeTankTier = engine.currentTankTier,
                unlockedTanks = engine.unlockedTanks.toList(),
                fishInstances = engine.fishList.toList(),
                bioLoadBalance = bioLoad,
                bioLoadMultiplier = multiplier,
                isMurkyWater = bioLoad < 30f,
                isSparklingWater = bioLoad > 80f,
                isHudVisible = engine.isHudVisible,
                isMaintainDrawerOpen = engine.isMaintainDrawerOpen,
                isShopSheetOpen = engine.isShopSheetOpen,
                isSettingsOpen = engine.isSettingsOpen,
                dailyBonusClaimable = engine.dailyBonusClaimable
            )
        }
    }

    fun unlockAndSwitchTank(tier: TankTier) {
        val success = engine.unlockAndSwitchTank(tier)
        if (success) {
            updateUiStateFromEngine()
            triggerDebouncedSaveAndSync()
        }
    }

    fun buyFish(speciesId: String) {
        val success = engine.addFish(speciesId)
        if (success) {
            updateUiStateFromEngine()
            triggerDebouncedSaveAndSync()
        }
    }

    fun performMaintenance(action: MaintenanceAction) {
        val success = engine.performMaintenanceAction(action)
        if (success) {
            updateUiStateFromEngine()
            triggerDebouncedSaveAndSync()
        }
    }

    fun claimDailyBonus() {
        engine.claimDailyBonus()
        updateUiStateFromEngine()
        triggerDebouncedSaveAndSync()
    }

    fun triggerFeedAction() {
        engine.triggerFeedAction()
        updateUiStateFromEngine()
    }

    /**
     * Debounces local DataStore save and background Railway REST sync.
     */
    fun triggerDebouncedSaveAndSync() {
        syncJob?.cancel()
        syncJob = viewModelScope.launch {
            val currentSave = AquariumSaveData(
                playerId = repository.saveDataFlow.first().playerId,
                carePoints = engine.carePoints,
                diamonds = engine.diamonds,
                playerLevel = engine.playerLevel,
                playerXp = engine.playerXp,
                activeTankTier = engine.currentTankTier.name,
                unlockedTanks = engine.unlockedTanks.map { it.name }.toSet(),
                ownedFishJson = "[${engine.fishList.joinToString(",") { "\"${it.speciesId}\"" }}]",
                bioLoadBalance = engine.bioLoadBalance,
                lastUpdated = System.currentTimeMillis()
            )

            // 1. Save locally for 0ms response
            repository.saveStateLocally(currentSave)

            // 2. Debounce 1.5s before syncing to Railway backend
            delay(1500)
            repository.syncPlayerStateToRailway(currentSave)
        }
    }

    fun onAppStop() {
        viewModelScope.launch {
            val currentSave = AquariumSaveData(
                playerId = repository.saveDataFlow.first().playerId,
                carePoints = engine.carePoints,
                diamonds = engine.diamonds,
                playerLevel = engine.playerLevel,
                playerXp = engine.playerXp,
                activeTankTier = engine.currentTankTier.name,
                unlockedTanks = engine.unlockedTanks.map { it.name }.toSet(),
                ownedFishJson = "[${engine.fishList.joinToString(",") { "\"${it.speciesId}\"" }}]",
                bioLoadBalance = engine.bioLoadBalance,
                lastUpdated = System.currentTimeMillis()
            )
            repository.saveStateLocally(currentSave)
            repository.syncPlayerStateToRailway(currentSave)
        }
    }
}
