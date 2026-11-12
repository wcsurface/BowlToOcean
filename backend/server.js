const express = require('express');
const cors = require('cors');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

// In-memory data persistence stores for player saves & leaderboard rankings
const playerSaves = new Map();
let leaderboardStore = [];

/**
 * GET /
 * Health check endpoint
 */
app.get('/', (req, res) => {
  res.json({
    status: 'online',
    app: 'Bowl to Ocean Aquarium Sim Backend',
    version: '1.0.0',
    timestamp: new Date().toISOString()
  });
});

/**
 * POST /api/sync
 * Upserts player save state
 */
app.post('/api/sync', (req, res) => {
  const {
    playerId,
    carePoints,
    diamonds,
    xp,
    activeTankTier,
    unlockedTanks,
    ownedFishJson,
    bioLoadJson,
    lastUpdated
  } = req.body;

  if (!playerId) {
    return res.status(400).json({ error: 'playerId is required' });
  }

  const saveData = {
    playerId,
    carePoints: carePoints || 0,
    diamonds: diamonds || 0,
    xp: xp || 0,
    activeTankTier: activeTankTier || 'BOWL',
    unlockedTanks: unlockedTanks || ['BOWL'],
    ownedFishJson: ownedFishJson || '[]',
    bioLoadJson: bioLoadJson || '{}',
    lastUpdated: lastUpdated || Date.now(),
    serverSyncedAt: Date.now()
  };

  playerSaves.set(playerId, saveData);

  console.log(`[SYNC] Saved state for player ${playerId} (Care Points: ${saveData.carePoints})`);

  return res.json({
    success: true,
    playerId,
    timestamp: saveData.serverSyncedAt
  });
});

/**
 * GET /api/config
 * Serves live remote-tunable game constants
 */
app.get('/api/config', (req, res) => {
  res.json({
    version: '1.0.0',
    configTimestamp: Date.now(),
    constants: {
      passiveCarePointsPerMin: 15,
      dailyBonusPoints: 500,
      diamondMultiplier: 1.2,
      fishCostMultiplier: 1.0,
      maintenanceRewardBonus: 1.5,
      maxFishCapacityMultiplier: 1.0,
      closedTestingNotice: 'Welcome to Bowl to Ocean Closed Testing! Economy tuners active.'
    }
  });
});

/**
 * POST /api/leaderboard
 * Top exhibit bio-load/collection ranking for Closed Testing feedback
 */
app.post('/api/leaderboard', (req, res) => {
  const { playerId, playerName, activeTankTier, bioLoadScore, fishCount } = req.body;

  if (!playerId) {
    return res.status(400).json({ error: 'playerId is required' });
  }

  const score = Number(bioLoadScore) || 0;
  const name = playerName || `Aquarist_${playerId.substring(0, 6)}`;

  // Upsert entry into leaderboard store
  const existingIdx = leaderboardStore.findIndex(entry => entry.playerId === playerId);
  const entryData = {
    playerId,
    playerName: name,
    activeTankTier: activeTankTier || 'BOWL',
    bioLoadScore: score,
    fishCount: Number(fishCount) || 1,
    updatedAt: Date.now()
  };

  if (existingIdx >= 0) {
    leaderboardStore[existingIdx] = entryData;
  } else {
    leaderboardStore.push(entryData);
  }

  // Sort descending by bioLoadScore
  leaderboardStore.sort((a, b) => b.bioLoadScore - a.bioLoadScore);

  // Determine player rank
  const playerRank = leaderboardStore.findIndex(entry => entry.playerId === playerId) + 1;

  res.json({
    success: true,
    playerRank,
    topExhibits: leaderboardStore.slice(0, 25)
  });
});

app.listen(PORT, () => {
  console.log(`Bowl to Ocean Backend server running on port ${PORT}`);
});
