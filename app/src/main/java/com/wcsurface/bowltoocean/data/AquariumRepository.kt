package com.wcsurface.bowltoocean.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.wcsurface.bowltoocean.model.TankTier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "aquarium_preferences")

data class AquariumSaveData(
    val playerId: String,
    val carePoints: Long = 1250L,
    val diamonds: Long = 25L,
    val playerLevel: Int = 3,
    val playerXp: Int = 180,
    val activeTankTier: String = TankTier.BOWL.name,
    val unlockedTanks: Set<String> = setOf(TankTier.BOWL.name),
    val ownedFishJson: String = "[]",
    val bioLoadBalance: Float = 85f,
    val lastUpdated: Long = System.currentTimeMillis()
)

data class RemoteGameConfig(
    val passiveCarePointsPerMin: Int = 15,
    val dailyBonusPoints: Int = 500,
    val diamondMultiplier: Float = 1.2f,
    val fishCostMultiplier: Float = 1.0f,
    val maintenanceRewardBonus: Float = 1.5f,
    val closedTestingNotice: String = "Welcome to Bowl to Ocean Closed Testing!"
)

class AquariumRepository(private val context: Context) {

    private object PreferencesKeys {
        val PLAYER_ID = stringPreferencesKey("player_id")
        val CARE_POINTS = longPreferencesKey("care_points")
        val DIAMONDS = longPreferencesKey("diamonds")
        val PLAYER_LEVEL = intPreferencesKey("player_level")
        val PLAYER_XP = intPreferencesKey("player_xp")
        val ACTIVE_TANK_TIER = stringPreferencesKey("active_tank_tier")
        val UNLOCKED_TANKS = stringSetPreferencesKey("unlocked_tanks")
        val OWNED_FISH_JSON = stringPreferencesKey("owned_fish_json")
        val BIO_LOAD_BALANCE = floatPreferencesKey("bio_load_balance")
        val LAST_UPDATED = longPreferencesKey("last_updated")
    }

    private val railwayBaseUrl = "https://backend-production-9e7da31c.up.railway.app"

    val saveDataFlow: Flow<AquariumSaveData> = context.dataStore.data.map { prefs ->
        val existingId = prefs[PreferencesKeys.PLAYER_ID]
        val validId: String = if (existingId.isNullOrBlank()) {
            UUID.randomUUID().toString()
        } else {
            existingId
        }

        AquariumSaveData(
            playerId = validId,
            carePoints = prefs[PreferencesKeys.CARE_POINTS] ?: 1250L,
            diamonds = prefs[PreferencesKeys.DIAMONDS] ?: 25L,
            playerLevel = prefs[PreferencesKeys.PLAYER_LEVEL] ?: 1,
            playerXp = prefs[PreferencesKeys.PLAYER_XP] ?: 0,
            activeTankTier = prefs[PreferencesKeys.ACTIVE_TANK_TIER] ?: TankTier.BOWL.name,
            unlockedTanks = prefs[PreferencesKeys.UNLOCKED_TANKS] ?: setOf(TankTier.BOWL.name),
            ownedFishJson = prefs[PreferencesKeys.OWNED_FISH_JSON] ?: "[]",
            bioLoadBalance = prefs[PreferencesKeys.BIO_LOAD_BALANCE] ?: 85f,
            lastUpdated = prefs[PreferencesKeys.LAST_UPDATED] ?: System.currentTimeMillis()
        )
    }

    suspend fun saveStateLocally(data: AquariumSaveData) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.PLAYER_ID] = data.playerId
            prefs[PreferencesKeys.CARE_POINTS] = data.carePoints
            prefs[PreferencesKeys.DIAMONDS] = data.diamonds
            prefs[PreferencesKeys.PLAYER_LEVEL] = data.playerLevel
            prefs[PreferencesKeys.PLAYER_XP] = data.playerXp
            prefs[PreferencesKeys.ACTIVE_TANK_TIER] = data.activeTankTier
            prefs[PreferencesKeys.UNLOCKED_TANKS] = data.unlockedTanks
            prefs[PreferencesKeys.OWNED_FISH_JSON] = data.ownedFishJson
            prefs[PreferencesKeys.BIO_LOAD_BALANCE] = data.bioLoadBalance
            prefs[PreferencesKeys.LAST_UPDATED] = System.currentTimeMillis()
        }
    }

    /**
     * Fetches live remote-tunable game constants from Railway backend API.
     * Gracefully falls back to default values if offline.
     */
    suspend fun fetchRemoteConfig(): RemoteGameConfig = withContext(Dispatchers.IO) {
        try {
            val url = URL("$railwayBaseUrl/api/config")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 3500
                readTimeout = 3500
            }

            if (connection.responseCode == 200) {
                val responseStr = BufferedReader(InputStreamReader(connection.inputStream)).use { it.readText() }
                val rootJson = JSONObject(responseStr)
                if (rootJson.has("constants")) {
                    val constants = rootJson.getJSONObject("constants")
                    return@withContext RemoteGameConfig(
                        passiveCarePointsPerMin = constants.optInt("passiveCarePointsPerMin", 15),
                        dailyBonusPoints = constants.optInt("dailyBonusPoints", 500),
                        diamondMultiplier = constants.optDouble("diamondMultiplier", 1.2).toFloat(),
                        fishCostMultiplier = constants.optDouble("fishCostMultiplier", 1.0).toFloat(),
                        maintenanceRewardBonus = constants.optDouble("maintenanceRewardBonus", 1.5).toFloat(),
                        closedTestingNotice = constants.optString("closedTestingNotice", "Welcome to Bowl to Ocean Closed Testing!")
                    )
                }
            }
        } catch (_: Exception) {
            // Offline or network unavailable fallback
        }
        return@withContext RemoteGameConfig()
    }

    /**
     * Upserts player save state to Railway cloud backend.
     */
    suspend fun syncPlayerStateToRailway(data: AquariumSaveData): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("$railwayBaseUrl/api/sync")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
                connectTimeout = 4000
                readTimeout = 4000
            }

            val bodyJson = JSONObject().apply {
                put("playerId", data.playerId)
                put("carePoints", data.carePoints)
                put("diamonds", data.diamonds)
                put("xp", data.playerXp)
                put("activeTankTier", data.activeTankTier)
                put("unlockedTanks", data.unlockedTanks.joinToString(","))
                put("ownedFishJson", data.ownedFishJson)
                put("bioLoadJson", "{\"balance\":${data.bioLoadBalance}}")
                put("lastUpdated", data.lastUpdated)
            }

            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(bodyJson.toString())
                writer.flush()
            }

            return@withContext (connection.responseCode == 200)
        } catch (_: Exception) {
            return@withContext false
        }
    }

    private fun String?.isNull_or_blank(): Boolean = this == null || this.trim().isEmpty()
}
