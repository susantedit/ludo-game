// Ludora 3D Visual & Physics Engine (Laboratory Edition)

(function () {
  'use strict';

  // ==========================================
  // 1. REAL 3D ISOMETRIC TUMBLING DICE ENGINE
  // ==========================================
  const diceCanvas = document.getElementById('diceCanvas');
  const diceCtx = diceCanvas.getContext('2d');
  const dicePill = document.getElementById('diceValuePill');
  const rollBtn = document.getElementById('rollDiceBtn');

  const CUBE_VERTICES = [
    [-1, -1, -1], [1, -1, -1], [1, 1, -1], [-1, 1, -1],
    [-1, -1, 1], [1, -1, 1], [1, 1, 1], [-1, 1, 1]
  ];

  const CUBE_FACES = [
    { value: 1, normal: [0, 0, 1], indices: [4, 5, 6, 7] },  // Front (+Z): 1
    { value: 6, normal: [0, 0, -1], indices: [1, 0, 3, 2] }, // Back (-Z): 6
    { value: 2, normal: [0, 1, 0], indices: [7, 6, 2, 3] },  // Top (+Y): 2
    { value: 5, normal: [0, -1, 0], indices: [4, 0, 1, 5] }, // Bottom (-Y): 5
    { value: 3, normal: [1, 0, 0], indices: [5, 1, 2, 6] },  // Right (+X): 3
    { value: 4, normal: [-1, 0, 0], indices: [0, 4, 7, 3] }  // Left (-X): 4
  ];

  // Matrix math helpers for 3D rotation
  function rotXMat(a) {
    const c = Math.cos(a), s = Math.sin(a);
    return [[1, 0, 0], [0, c, -s], [0, s, c]];
  }
  function rotYMat(a) {
    const c = Math.cos(a), s = Math.sin(a);
    return [[c, 0, s], [0, 1, 0], [-s, 0, c]];
  }
  function rotZMat(a) {
    const c = Math.cos(a), s = Math.sin(a);
    return [[c, -s, 0], [s, c, 0], [0, 0, 1]];
  }
  function matMul(a, b) {
    const res = [[0, 0, 0], [0, 0, 0], [0, 0, 0]];
    for (let r = 0; r < 3; r++) {
      for (let c = 0; c < 3; c++) {
        for (let k = 0; k < 3; k++) {
          res[r][c] += a[r][k] * b[k][c];
        }
      }
    }
    return res;
  }
  function matMulVec(m, v) {
    return [
      m[0][0] * v[0] + m[0][1] * v[1] + m[0][2] * v[2],
      m[1][0] * v[0] + m[1][1] * v[1] + m[1][2] * v[2],
      m[2][0] * v[0] + m[2][1] * v[1] + m[2][2] * v[2]
    ];
  }

  // Exact rotations that map each die face normal to +Y (top orientation)
  const FACE_TO_TOP_MATRICES = {
    1: rotXMat(-Math.PI / 2),
    2: rotXMat(0),
    3: rotZMat(Math.PI / 2),
    4: rotZMat(-Math.PI / 2),
    5: rotXMat(Math.PI),
    6: rotXMat(Math.PI / 2)
  };
  const R_CAM = matMul(rotXMat(0.615), rotYMat(0.785));

  function getTargetMatrix(face) {
    const fMat = FACE_TO_TOP_MATRICES[face] || FACE_TO_TOP_MATRICES[6];
    return matMul(R_CAM, fMat);
  }

  let currentDiceMatrix = getTargetMatrix(6);
  let velX = 0;
  let velY = 0;
  let velZ = 0;
  let altitude = 0;
  let altVel = 0;
  let isRolling = false;
  let currentDiceFace = 6;

  function project(v, size, cx, cy) {
    const depth = 1 + v[2] * 0.15;
    return [cx + v[0] * size * depth, cy - v[1] * size * depth];
  }

  function rollDice() {
    if (isRolling) return;
    isRolling = true;
    velX = (Math.random() * 0.4 + 0.3) * (Math.random() < 0.5 ? 1 : -1);
    velY = (Math.random() * 0.4 + 0.3) * (Math.random() < 0.5 ? 1 : -1);
    velZ = (Math.random() * 0.2 + 0.1);
    altVel = 18;
    currentDiceFace = Math.floor(Math.random() * 6) + 1;
    dicePill.textContent = 'Rolling...';
  }

  rollBtn.addEventListener('click', rollDice);
  window.addEventListener('keydown', (e) => {
    if (e.code === 'Space') rollDice();
  });

  function renderDice() {
    diceCtx.clearRect(0, 0, diceCanvas.width, diceCanvas.height);
    const cx = diceCanvas.width / 2;
    const cy = diceCanvas.height / 2 + 10;
    const size = 68;

    // Physics update
    if (isRolling) {
      const deltaR = matMul(rotZMat(velZ), matMul(rotYMat(velY), rotXMat(velX)));
      currentDiceMatrix = matMul(deltaR, currentDiceMatrix);
      velX *= 0.96;
      velY *= 0.96;
      velZ *= 0.96;

      altitude += altVel;
      altVel -= 1.4; // gravity
      if (altitude <= 0) {
        altitude = 0;
        altVel = -altVel * 0.58; // bounce restitution
        if (Math.abs(altVel) < 1 && Math.abs(velX) < 0.05 && Math.abs(velY) < 0.05) {
          isRolling = false;
          // Snap target matrix so the rolled face is guaranteed to be on top
          currentDiceMatrix = getTargetMatrix(currentDiceFace);
          dicePill.textContent = `Rolled: ${currentDiceFace} (On Top)`;
        }
      }
    }

    const currentY = cy - altitude;

    // Ambient drop shadow
    const shadowScale = Math.max(0.4, 1 - (altitude / 160));
    const shadowAlpha = Math.max(0.1, 0.45 - (altitude / 200));
    diceCtx.save();
    diceCtx.beginPath();
    diceCtx.ellipse(cx, cy + size * 0.85, size * 1.3 * shadowScale, size * 0.45 * shadowScale, 0, 0, Math.PI * 2);
    diceCtx.fillStyle = `rgba(0, 0, 0, ${shadowAlpha})`;
    diceCtx.filter = 'blur(8px)';
    diceCtx.fill();
    diceCtx.restore();

    // Transform vertices
    const rotVertices = CUBE_VERTICES.map(v => matMulVec(currentDiceMatrix, v));
    const projVertices = rotVertices.map(v => project(v, size, cx, currentY));

    // Sort faces by depth
    const lightDir = [-0.45, -0.65, 0.6];
    const lenL = Math.sqrt(lightDir[0]**2 + lightDir[1]**2 + lightDir[2]**2);
    const nl = lightDir.map(n => n / lenL);

    const sortedFaces = CUBE_FACES.map(f => {
      const rotNorm = matMulVec(currentDiceMatrix, f.normal);
      const isVisible = rotNorm[2] > 0.04;
      const dot = Math.max(0.15, rotNorm[0] * nl[0] + rotNorm[1] * nl[1] + rotNorm[2] * nl[2]);
      return { ...f, rotNorm, isVisible, dot };
    }).sort((a, b) => a.rotNorm[2] - b.rotNorm[2]);

    // Draw visible faces
    sortedFaces.forEach(face => {
      if (!face.isVisible) return;
      const quad = face.indices.map(idx => projVertices[idx]);

      diceCtx.save();
      diceCtx.beginPath();
      diceCtx.moveTo(quad[0][0], quad[0][1]);
      for (let i = 1; i < 4; i++) diceCtx.lineTo(quad[i][0], quad[i][1]);
      diceCtx.closePath();

      // Ivory base shading
      const baseTone = Math.floor(245 * face.dot);
      diceCtx.fillStyle = `rgb(${baseTone + 8}, ${baseTone + 5}, ${baseTone - 10})`;
      diceCtx.fill();

      // Rounded edge stroke
      diceCtx.strokeStyle = 'rgba(255, 255, 255, 0.4)';
      diceCtx.lineWidth = 2.5;
      diceCtx.stroke();
      diceCtx.restore();

      // Draw Gold Pips
      drawPips(face.value, quad);
    });

    requestAnimationFrame(renderDice);
  }

  function drawPips(value, quad) {
    function interp(u, v) {
      const topX = quad[0][0] + (quad[1][0] - quad[0][0]) * u;
      const topY = quad[0][1] + (quad[1][1] - quad[0][1]) * u;
      const botX = quad[3][0] + (quad[2][0] - quad[3][0]) * u;
      const botY = quad[3][1] + (quad[2][1] - quad[3][1]) * u;
      return [topX + (botX - topX) * v, topY + (botY - topY) * v];
    }

    const pipMap = {
      1: [[0.5, 0.5]],
      2: [[0.28, 0.28], [0.72, 0.72]],
      3: [[0.28, 0.28], [0.5, 0.5], [0.72, 0.72]],
      4: [[0.28, 0.28], [0.72, 0.28], [0.28, 0.72], [0.72, 0.72]],
      5: [[0.28, 0.28], [0.72, 0.28], [0.5, 0.5], [0.28, 0.72], [0.72, 0.72]],
      6: [
        [0.28, 0.22], [0.72, 0.22],
        [0.28, 0.5], [0.72, 0.5],
        [0.28, 0.78], [0.72, 0.78]
      ]
    };

    const pips = pipMap[value] || [];
    pips.forEach(([u, v]) => {
      const [px, py] = interp(u, v);
      diceCtx.save();
      diceCtx.beginPath();
      diceCtx.arc(px, py, 6.2, 0, Math.PI * 2);

      // Gold Metallic Gradient
      const grad = diceCtx.createRadialGradient(px - 1.5, py - 1.5, 1, px, py, 6.2);
      grad.addColorStop(0, '#FFE888');
      grad.addColorStop(0.4, '#D4AF37');
      grad.addColorStop(1, '#664E11');
      diceCtx.fillStyle = grad;
      diceCtx.fill();

      // Deep inset inner shadow
      diceCtx.strokeStyle = 'rgba(40, 25, 5, 0.7)';
      diceCtx.lineWidth = 1.2;
      diceCtx.stroke();
      diceCtx.restore();
    });
  }

  // ==========================================
  // 2. REAL 3D ANIMATED VIPER ENGINE
  // ==========================================
  const snakeCanvas = document.getElementById('snakeCanvas');
  const snakeCtx = snakeCanvas.getContext('2d');
  const snakePill = document.getElementById('snakeStatePill');
  const swallowBtn = document.getElementById('swallowTokenBtn');

  let snakeTime = 0;
  let swallowProgress = -1;

  swallowBtn.addEventListener('click', () => {
    swallowProgress = 0;
    snakePill.textContent = 'Ingesting Token...';
  });

  function evalBezier(p0, p1, p2, p3, t) {
    const u = 1 - t;
    return [
      u**3 * p0[0] + 3 * u**2 * t * p1[0] + 3 * u * t**2 * p2[0] + t**3 * p3[0],
      u**3 * p0[1] + 3 * u**2 * t * p1[1] + 3 * u * t**2 * p2[1] + t**3 * p3[1]
    ];
  }

  function renderSnake() {
    snakeCtx.clearRect(0, 0, snakeCanvas.width, snakeCanvas.height);
    snakeTime += 0.035;

    if (swallowProgress >= 0) {
      swallowProgress += 0.015;
      if (swallowProgress > 1.05) {
        swallowProgress = -1;
        snakePill.textContent = 'Slithering • 60 FPS';
      }
    }

    const p0 = [90, 60];    // Head
    const p1 = [320, 80];   // Control 1
    const p2 = [40, 280];   // Control 2
    const p3 = [330, 290];  // Tail

    const samples = 48;
    const baseSpine = [];
    for (let i = 0; i <= samples; i++) {
      baseSpine.push(evalBezier(p0, p1, p2, p3, i / samples));
    }

    // Apply Muscular Sine Wave Undulation
    const undulated = [];
    for (let i = 0; i <= samples; i++) {
      const t = i / samples;
      const pt = baseSpine[i];
      const prev = i > 0 ? baseSpine[i - 1] : pt;
      const next = i < samples ? baseSpine[i + 1] : pt;
      const dx = next[0] - prev[0];
      const dy = next[1] - prev[1];
      const len = Math.hypot(dx, dy) || 1;
      const nx = -dy / len;
      const ny = dx / len;

      const envelope = Math.sin(t * Math.PI);
      const phase = (t * 5.5 * Math.PI) - (snakeTime * 4.2);
      const disp = 14 * envelope * Math.sin(phase);

      undulated.push([pt[0] + nx * disp, pt[1] + ny * disp]);
    }

    // Generate Body Boundaries & Scales
    const lefts = [];
    const rights = [];
    const scales = [];

    for (let i = 0; i <= samples; i++) {
      const t = i / samples;
      const pt = undulated[i];
      const prev = i > 0 ? undulated[i - 1] : pt;
      const next = i < samples ? undulated[i + 1] : pt;
      const dx = next[0] - prev[0];
      const dy = next[1] - prev[1];
      const len = Math.hypot(dx, dy) || 1;
      const nx = -dy / len;
      const ny = dx / len;

      let r = 3 + (19 - 3) * (1 - t)**0.85;

      // Swallowing bulge wave
      if (swallowProgress >= 0) {
        const dist = Math.abs(t - swallowProgress);
        if (dist < 0.16) {
          r += 9.5 * Math.cos((dist / 0.16) * (Math.PI / 2));
        }
      }

      lefts.push([pt[0] - nx * r, pt[1] - ny * r]);
      rights.push([pt[0] + nx * r, pt[1] + ny * r]);

      if (i % 2 === 0 && i < samples) {
        scales.push({ center: pt, normal: [nx, ny], r: r * 0.72, isGold: (i % 4 === 0) });
      }
    }

    // 1. Draw Body Ambient Contact Shadow
    snakeCtx.save();
    snakeCtx.beginPath();
    snakeCtx.moveTo(lefts[0][0] + 6, lefts[0][1] + 8);
    for (let i = 1; i <= samples; i++) snakeCtx.lineTo(lefts[i][0] + 6, lefts[i][1] + 8);
    for (let i = samples; i >= 0; i--) snakeCtx.lineTo(rights[i][0] + 6, rights[i][1] + 8);
    snakeCtx.closePath();
    snakeCtx.fillStyle = 'rgba(0, 0, 0, 0.45)';
    snakeCtx.filter = 'blur(6px)';
    snakeCtx.fill();
    snakeCtx.restore();

    // 2. Draw Tapered Emerald Muscular Body Mesh
    snakeCtx.save();
    snakeCtx.beginPath();
    snakeCtx.moveTo(lefts[0][0], lefts[0][1]);
    for (let i = 1; i <= samples; i++) snakeCtx.lineTo(lefts[i][0], lefts[i][1]);
    for (let i = samples; i >= 0; i--) snakeCtx.lineTo(rights[i][0], rights[i][1]);
    snakeCtx.closePath();

    const bodyGrad = snakeCtx.createLinearGradient(90, 60, 330, 290);
    bodyGrad.addColorStop(0, '#00A86B');
    bodyGrad.addColorStop(0.5, '#0B4F37');
    bodyGrad.addColorStop(1, '#022417');
    snakeCtx.fillStyle = bodyGrad;
    snakeCtx.fill();
    snakeCtx.strokeStyle = 'rgba(212, 175, 55, 0.4)';
    snakeCtx.lineWidth = 1.2;
    snakeCtx.stroke();
    snakeCtx.restore();

    // 3. Draw Iridescent Diamond Scales
    scales.forEach(s => {
      snakeCtx.save();
      snakeCtx.beginPath();
      snakeCtx.ellipse(s.center[0], s.center[1], s.r, s.r * 0.45, Math.atan2(s.normal[1], s.normal[0]), 0, Math.PI * 2);
      snakeCtx.fillStyle = s.isGold ? 'rgba(229, 169, 60, 0.45)' : 'rgba(0, 220, 130, 0.28)';
      snakeCtx.fill();
      snakeCtx.restore();
    });

    // 4. Draw Viper Head, Blinking Amber Eyes & Forked Tongue
    const headPt = undulated[0];
    const headNext = undulated[1];
    const hdx = headPt[0] - headNext[0];
    const hdy = headPt[1] - headNext[1];
    const hlen = Math.hypot(hdx, hdy) || 1;
    const fx = hdx / hlen;
    const fy = hdy / hlen;
    const rx = -fy;
    const ry = fx;

    // Viper Triangular Head
    const snout = [headPt[0] + fx * 26, headPt[1] + fy * 26];
    const leftJaw = [headPt[0] - rx * 20, headPt[1] - ry * 20];
    const rightJaw = [headPt[0] + rx * 20, headPt[1] + ry * 20];

    snakeCtx.save();
    snakeCtx.beginPath();
    snakeCtx.moveTo(snout[0], snout[1]);
    snakeCtx.lineTo(leftJaw[0], leftJaw[1]);
    snakeCtx.lineTo(headPt[0], headPt[1]);
    snakeCtx.lineTo(rightJaw[0], rightJaw[1]);
    snakeCtx.closePath();
    snakeCtx.fillStyle = '#083E2B';
    snakeCtx.fill();
    snakeCtx.strokeStyle = '#D4AF37';
    snakeCtx.lineWidth = 2;
    snakeCtx.stroke();

    // Flickering Bifid Tongue
    const tongueDart = Math.sin(snakeTime * 12) > 0.4;
    if (tongueDart) {
      const tLen = 18;
      const tTip = [snout[0] + fx * tLen, snout[1] + fy * tLen];
      snakeCtx.beginPath();
      snakeCtx.moveTo(snout[0], snout[1]);
      snakeCtx.lineTo(tTip[0], tTip[1]);
      snakeCtx.lineTo(tTip[0] + fx * 7 - rx * 5, tTip[1] + fy * 7 - ry * 5);
      snakeCtx.moveTo(tTip[0], tTip[1]);
      snakeCtx.lineTo(tTip[0] + fx * 7 + rx * 5, tTip[1] + fy * 7 + ry * 5);
      snakeCtx.strokeStyle = '#FF3344';
      snakeCtx.lineWidth = 2.2;
      snakeCtx.stroke();
    }

    // Predatory Amber Eyes with Slit Pupils
    const eyeDistFwd = 12;
    const eyeDistSide = 12;
    const leftEye = [headPt[0] + fx * eyeDistFwd - rx * eyeDistSide, headPt[1] + fy * eyeDistFwd - ry * eyeDistSide];
    const rightEye = [headPt[0] + fx * eyeDistFwd + rx * eyeDistSide, headPt[1] + fy * eyeDistFwd + ry * eyeDistSide];

    [leftEye, rightEye].forEach(eye => {
      snakeCtx.beginPath();
      snakeCtx.arc(eye[0], eye[1], 4.5, 0, Math.PI * 2);
      snakeCtx.fillStyle = '#FFAA00';
      snakeCtx.fill();

      // Slit Pupil
      snakeCtx.beginPath();
      snakeCtx.ellipse(eye[0], eye[1], 1.2, 3.8, Math.atan2(fy, fx), 0, Math.PI * 2);
      snakeCtx.fillStyle = '#0B0E14';
      snakeCtx.fill();
    });

    snakeCtx.restore();

    requestAnimationFrame(renderSnake);
  }

  // ==========================================
  // 3. KATHMANDU CULTURAL WORLD BOARD RENDERER
  // ==========================================
  // 3. AUTHENTIC 15x15 KATHMANDU CULTURAL LUDO BOARD RENDERER
  // ==========================================
  const boardCanvas = document.getElementById('boardCanvas');
  const boardCtx = boardCanvas.getContext('2d');

  function renderKathmanduBoard() {
    const w = boardCanvas.width;
    const h = boardCanvas.height;
    boardCtx.clearRect(0, 0, w, h);

    const pad = 12;
    const boardSize = Math.min(w - pad * 2, h - pad * 2);
    const ox = (w - boardSize) / 2;
    const oy = (h - boardSize) / 2;

    const borderThick = boardSize * 0.042;
    const innerSize = boardSize - borderThick * 2;
    const gridOx = ox + borderThick;
    const gridOy = oy + borderThick;
    const cellSize = innerSize / 15;

    // Helper: cell to pixel coordinates
    const cellX = (c) => gridOx + c * cellSize;
    const cellY = (r) => gridOy + r * cellSize;
    const cellCenter = (c, r) => [cellX(c) + cellSize / 2, cellY(r) + cellSize / 2];

    // 1. CARVED DARK WALNUT WOOD FRAME
    boardCtx.save();
    const woodGrad = boardCtx.createLinearGradient(ox, oy, ox + boardSize, oy + boardSize);
    woodGrad.addColorStop(0, '#1E130B');
    woodGrad.addColorStop(0.3, '#2A1A0F');
    woodGrad.addColorStop(0.7, '#1E130B');
    woodGrad.addColorStop(1, '#160E08');
    boardCtx.fillStyle = woodGrad;
    boardCtx.fillRect(ox, oy, boardSize, boardSize);

    // Subtle wood grain lines
    boardCtx.strokeStyle = 'rgba(255, 255, 255, 0.04)';
    boardCtx.lineWidth = 1;
    for (let i = 4; i < boardSize; i += 10) {
      boardCtx.beginPath();
      boardCtx.moveTo(ox + i, oy);
      boardCtx.lineTo(ox + i, oy + boardSize);
      boardCtx.stroke();
    }

    // Outer and Inner Gold Inlay Trim
    boardCtx.strokeStyle = '#D4AF37';
    boardCtx.lineWidth = 2.5;
    boardCtx.strokeRect(ox, oy, boardSize, boardSize);
    boardCtx.strokeRect(gridOx - 1, gridOy - 1, innerSize + 2, innerSize + 2);
    boardCtx.restore();

    // 2. ORNATE NEWARI BRASS CORNERS & PRAYER FLAG TASSELS
    const flagColors = ['#0066CC', '#EEEEEE', '#CC2222', '#008844', '#FFCC00'];
    const corners = [
      [ox + 4, oy + 4, 1, 1],
      [ox + boardSize - 4, oy + 4, -1, 1],
      [ox + boardSize - 4, oy + boardSize - 4, -1, -1],
      [ox + 4, oy + boardSize - 4, 1, -1]
    ];

    corners.forEach(([kx, ky, dx, dy]) => {
      // Corner brass bracket
      boardCtx.save();
      boardCtx.strokeStyle = '#FFE090';
      boardCtx.lineWidth = 2;
      boardCtx.beginPath();
      boardCtx.moveTo(kx, ky + dy * 18);
      boardCtx.lineTo(kx, ky);
      boardCtx.lineTo(kx + dx * 18, ky);
      boardCtx.stroke();
      boardCtx.restore();

      // Hanging 5-color prayer flag tassels
      flagColors.forEach((col, idx) => {
        boardCtx.save();
        boardCtx.beginPath();
        const tLen = 14 + idx * 3.5;
        const angle = (idx - 2) * 0.18 + (dx < 0 ? Math.PI : 0);
        boardCtx.moveTo(kx, ky);
        boardCtx.lineTo(kx + Math.cos(angle) * tLen, ky + Math.sin(angle) * tLen);
        boardCtx.strokeStyle = col;
        boardCtx.lineWidth = 2.4;
        boardCtx.lineCap = 'round';
        boardCtx.stroke();
        boardCtx.restore();
      });
    });

    // 3. DRAW FOUR 6x6 COURTYARD YARDS (CHOWKS)
    function drawYard(startCol, startRow, colorHex, accentHex, labelText, palaceName) {
      const x = cellX(startCol);
      const y = cellY(startRow);
      const yardWidth = cellSize * 6;

      boardCtx.save();
      // Courtyard floor
      const yardGrad = boardCtx.createRadialGradient(x + yardWidth/2, y + yardWidth/2, 10, x + yardWidth/2, y + yardWidth/2, yardWidth/2);
      yardGrad.addColorStop(0, colorHex);
      yardGrad.addColorStop(1, '#0C0F17');
      boardCtx.fillStyle = yardGrad;
      boardCtx.fillRect(x, y, yardWidth, yardWidth);

      // Yard brass border
      boardCtx.strokeStyle = accentHex;
      boardCtx.lineWidth = 2;
      boardCtx.strokeRect(x + 1, y + 1, yardWidth - 2, yardWidth - 2);

      // Inner raised courtyard pedestal
      const innerMargin = cellSize * 0.85;
      const innerW = yardWidth - innerMargin * 2;
      boardCtx.fillStyle = '#141824';
      boardCtx.fillRect(x + innerMargin, y + innerMargin, innerW, innerW);
      boardCtx.strokeStyle = '#D4AF37';
      boardCtx.lineWidth = 1.5;
      boardCtx.strokeRect(x + innerMargin, y + innerMargin, innerW, innerW);

      // Palace Label
      boardCtx.fillStyle = 'rgba(255, 224, 144, 0.75)';
      boardCtx.font = `600 ${Math.max(9, cellSize * 0.32)}px Cinzel, serif`;
      boardCtx.textAlign = 'center';
      boardCtx.fillText(palaceName, x + yardWidth / 2, y + innerMargin * 0.65);

      // 4 Circular Token Slots (Bases)
      const slotOffsets = [
        [x + cellSize * 2.0, y + cellSize * 2.0],
        [x + cellSize * 4.0, y + cellSize * 2.0],
        [x + cellSize * 2.0, y + cellSize * 4.0],
        [x + cellSize * 4.0, y + cellSize * 4.0]
      ];

      slotOffsets.forEach(([sx, sy], sIdx) => {
        boardCtx.save();
        // Recessed base rim
        boardCtx.beginPath();
        boardCtx.arc(sx, sy, cellSize * 0.65, 0, Math.PI * 2);
        boardCtx.fillStyle = '#080B11';
        boardCtx.fill();
        boardCtx.strokeStyle = accentHex;
        boardCtx.lineWidth = 1.8;
        boardCtx.stroke();

        // Inner gold ring
        boardCtx.beginPath();
        boardCtx.arc(sx, sy, cellSize * 0.45, 0, Math.PI * 2);
        boardCtx.strokeStyle = 'rgba(212, 175, 55, 0.45)';
        boardCtx.stroke();

        // 2 Resting Tokens inside each home yard
        if (sIdx < 2) {
          draw3DToken(sx, sy, cellSize * 0.42, colorHex);
        }
        boardCtx.restore();
      });

      boardCtx.restore();
    }

    // Draw the 4 authentic player yards
    drawYard(0, 0, '#9E2A1A', '#FF7D6B', 'RED', 'Patan Chowk');       // Top-Left (Red)
    drawYard(9, 0, '#0E5C38', '#5AE49A', 'GREEN', 'Bhaktapur');       // Top-Right (Green)
    drawYard(9, 9, '#A6730A', '#FFDF75', 'YELLOW', 'Basantapur');     // Bottom-Right (Yellow)
    drawYard(0, 9, '#154182', '#7AA8FF', 'BLUE', 'Kirtipur');        // Bottom-Left (Blue)

    // 4. DRAW 72 CROSS TRACK CELLS
    // Helper to identify cell role
    function getCellInfo(c, r) {
      // Check if inside yards
      if ((c < 6 && r < 6) || (c > 8 && r < 6) || (c < 6 && r > 8) || (c > 8 && r > 8)) {
        return { isTrack: false };
      }
      // Check if inside center
      if (c >= 6 && c <= 8 && r >= 6 && r <= 8) {
        return { isCenter: true, isTrack: false };
      }

      // Check Home Columns
      if (c === 7 && r >= 1 && r <= 5) return { isTrack: true, isHomePath: true, color: '#0E5C38', arrow: '↓' };
      if (c === 7 && r >= 9 && r <= 13) return { isTrack: true, isHomePath: true, color: '#154182', arrow: '↑' };
      if (r === 7 && c >= 1 && c <= 5) return { isTrack: true, isHomePath: true, color: '#9E2A1A', arrow: '→' };
      if (r === 7 && c >= 9 && c <= 13) return { isTrack: true, isHomePath: true, color: '#A6730A', arrow: '←' };

      // Safe Stars
      const starCells = [
        [1, 6], [6, 2], [8, 1], [12, 6], [13, 8], [8, 12], [6, 13], [2, 8]
      ];
      const isStar = starCells.some(([sc, sr]) => sc === c && sr === r);

      // Starting squares with player colors
      let startColor = null;
      if (c === 1 && r === 6) startColor = '#9E2A1A'; // Red start
      if (c === 8 && r === 1) startColor = '#0E5C38'; // Green start
      if (c === 13 && r === 8) startColor = '#A6730A'; // Yellow start
      if (c === 6 && r === 13) startColor = '#154182'; // Blue start

      return { isTrack: true, isStar, isStart: !!startColor, startColor };
    }

    // Render all track tiles
    for (let r = 0; r < 15; r++) {
      for (let c = 0; c < 15; c++) {
        const info = getCellInfo(c, r);
        if (!info.isTrack) continue;

        const x = cellX(c);
        const y = cellY(r);

        boardCtx.save();
        // Tile Background
        if (info.isHomePath) {
          boardCtx.fillStyle = info.color;
        } else if (info.isStart) {
          boardCtx.fillStyle = info.startColor;
        } else {
          boardCtx.fillStyle = (r + c) % 2 === 0 ? '#1C212E' : '#141822';
        }
        boardCtx.fillRect(x, y, cellSize, cellSize);

        // Tile Brass Grid Border
        boardCtx.strokeStyle = 'rgba(212, 175, 55, 0.4)';
        boardCtx.lineWidth = 1;
        boardCtx.strokeRect(x, y, cellSize, cellSize);

        // Directional Home Column Chevrons
        if (info.isHomePath && info.arrow) {
          boardCtx.fillStyle = 'rgba(255, 224, 144, 0.85)';
          boardCtx.font = `700 ${cellSize * 0.45}px sans-serif`;
          boardCtx.textAlign = 'center';
          boardCtx.textBaseline = 'middle';
          boardCtx.fillText(info.arrow, x + cellSize / 2, y + cellSize / 2 + 1);
        }

        // 8-Pointed Golden Nepali Mandala Stars on Safe Squares
        if (info.isStar) {
          drawNepaliMandalaStar(x + cellSize / 2, y + cellSize / 2, cellSize * 0.38);
        }

        boardCtx.restore();
      }
    }

    // Helper: Draw 8-pointed Nepali mandala star
    function drawNepaliMandalaStar(cx, cy, r) {
      boardCtx.save();
      boardCtx.fillStyle = '#FFE090';
      boardCtx.strokeStyle = '#D4AF37';
      boardCtx.lineWidth = 1.2;
      boardCtx.beginPath();
      for (let i = 0; i < 16; i++) {
        const rad = (i % 2 === 0) ? r : r * 0.45;
        const ang = (i * Math.PI) / 8 - Math.PI / 2;
        const px = cx + Math.cos(ang) * rad;
        const py = cy + Math.sin(ang) * rad;
        if (i === 0) boardCtx.moveTo(px, py);
        else boardCtx.lineTo(px, py);
      }
      boardCtx.closePath();
      boardCtx.fill();
      boardCtx.stroke();
      boardCtx.restore();
    }

    // 5. DRAW CENTRAL 3x3 HOME TRIANGLES
    const goalX = cellX(6);
    const goalY = cellY(6);
    const goalW = cellSize * 3;
    const centerPt = [goalX + goalW / 2, goalY + goalW / 2];

    const homeTriangles = [
      { p1: [goalX, goalY], p2: [goalX, goalY + goalW], color: '#9E2A1A' },           // Left (Red)
      { p1: [goalX, goalY], p2: [goalX + goalW, goalY], color: '#0E5C38' },           // Top (Green)
      { p1: [goalX + goalW, goalY], p2: [goalX + goalW, goalY + goalW], color: '#A6730A' }, // Right (Yellow)
      { p1: [goalX, goalY + goalW], p2: [goalX + goalW, goalY + goalW], color: '#154182' }  // Bottom (Blue)
    ];

    homeTriangles.forEach(tri => {
      boardCtx.save();
      boardCtx.beginPath();
      boardCtx.moveTo(centerPt[0], centerPt[1]);
      boardCtx.lineTo(tri.p1[0], tri.p1[1]);
      boardCtx.lineTo(tri.p2[0], tri.p2[1]);
      boardCtx.closePath();
      boardCtx.fillStyle = tri.color;
      boardCtx.fill();
      boardCtx.strokeStyle = 'rgba(212, 175, 55, 0.6)';
      boardCtx.lineWidth = 1.5;
      boardCtx.stroke();
      boardCtx.restore();
    });

    // 6. CENTRAL SWAYAMBHUNATH WISDOM EYES MEDALLION
    const medR = cellSize * 1.35;
    const [mcx, mcy] = centerPt;

    // Outer Lotus Petals (16 sculpted petals)
    boardCtx.save();
    for (let p = 0; p < 16; p++) {
      const pAng = (p * Math.PI * 2) / 16;
      const px = mcx + Math.cos(pAng) * (medR + 4);
      const py = mcy + Math.sin(pAng) * (medR + 4);
      boardCtx.save();
      boardCtx.translate(px, py);
      boardCtx.rotate(pAng + Math.PI / 2);
      boardCtx.fillStyle = (p % 2 === 0) ? '#D4AF37' : '#9E2A1A';
      boardCtx.beginPath();
      boardCtx.ellipse(0, 0, 3.5, 8, 0, 0, Math.PI * 2);
      boardCtx.fill();
      boardCtx.restore();
    }

    // Gold Medallion Disk
    const goldGrad = boardCtx.createRadialGradient(mcx - medR * 0.3, mcy - medR * 0.3, 4, mcx, mcy, medR);
    goldGrad.addColorStop(0, '#FFF1B8');
    goldGrad.addColorStop(0.55, '#D4AF37');
    goldGrad.addColorStop(1, '#7A5B0B');
    boardCtx.fillStyle = goldGrad;
    boardCtx.beginPath();
    boardCtx.arc(mcx, mcy, medR, 0, Math.PI * 2);
    boardCtx.fill();
    boardCtx.strokeStyle = '#2B1C03';
    boardCtx.lineWidth = 2.5;
    boardCtx.stroke();

    // Swayambhunath Eyes & Unity Nose
    const eyeSpacing = medR * 0.38;
    const eyeElevation = medR * 0.12;

    function drawWisdomEye(ex) {
      boardCtx.save();
      // White Sclera
      boardCtx.beginPath();
      boardCtx.moveTo(ex - 12, mcy - eyeElevation);
      boardCtx.quadraticCurveTo(ex, mcy - eyeElevation - 9, ex + 12, mcy - eyeElevation);
      boardCtx.quadraticCurveTo(ex, mcy - eyeElevation + 7, ex - 12, mcy - eyeElevation);
      boardCtx.fillStyle = '#FFFFFF';
      boardCtx.fill();
      boardCtx.strokeStyle = '#0B0E14';
      boardCtx.lineWidth = 1.8;
      boardCtx.stroke();

      // Blue/Black Iris
      boardCtx.beginPath();
      boardCtx.arc(ex, mcy - eyeElevation - 0.5, 4.2, 0, Math.PI * 2);
      boardCtx.fillStyle = '#0F2C59';
      boardCtx.fill();

      // Eyebrow
      boardCtx.beginPath();
      boardCtx.moveTo(ex - 14, mcy - eyeElevation - 6);
      boardCtx.quadraticCurveTo(ex, mcy - eyeElevation - 14, ex + 14, mcy - eyeElevation - 5);
      boardCtx.strokeStyle = '#0B0E14';
      boardCtx.lineWidth = 2;
      boardCtx.stroke();
      boardCtx.restore();
    }

    drawWisdomEye(mcx - eyeSpacing);
    drawWisdomEye(mcx + eyeSpacing);

    // Question-mark curl of unity ("Ek")
    boardCtx.beginPath();
    boardCtx.moveTo(mcx, mcy + medR * 0.05);
    boardCtx.lineTo(mcx, mcy + medR * 0.22);
    boardCtx.quadraticCurveTo(mcx + 7, mcy + medR * 0.32, mcx, mcy + medR * 0.42);
    boardCtx.strokeStyle = '#0B0E14';
    boardCtx.lineWidth = 2.2;
    boardCtx.stroke();

    // Red Urna (Third eye of wisdom)
    boardCtx.beginPath();
    boardCtx.arc(mcx, mcy - medR * 0.32, 2.5, 0, Math.PI * 2);
    boardCtx.fillStyle = '#CC2222';
    boardCtx.fill();
    boardCtx.restore();

    // 7. REAL TACTILE 3D TOKENS ON ACTIVE TRACK
    function draw3DToken(tx, ty, r, colHex) {
      boardCtx.save();
      // Drop Shadow
      boardCtx.beginPath();
      boardCtx.ellipse(tx + 2, ty + 3, r * 1.05, r * 0.55, 0, 0, Math.PI * 2);
      boardCtx.fillStyle = 'rgba(0, 0, 0, 0.45)';
      boardCtx.filter = 'blur(3px)';
      boardCtx.fill();
      boardCtx.filter = 'none';

      // Glossy Token Body
      const tokenGrad = boardCtx.createRadialGradient(tx - r * 0.3, ty - r * 0.35, r * 0.1, tx, ty, r);
      tokenGrad.addColorStop(0, '#FFFFFF');
      tokenGrad.addColorStop(0.25, colHex);
      tokenGrad.addColorStop(1, '#0B0E14');
      boardCtx.beginPath();
      boardCtx.arc(tx, ty, r, 0, Math.PI * 2);
      boardCtx.fillStyle = tokenGrad;
      boardCtx.fill();
      boardCtx.strokeStyle = '#D4AF37';
      boardCtx.lineWidth = 1.4;
      boardCtx.stroke();

      // Gold Crown Pip
      boardCtx.beginPath();
      boardCtx.arc(tx, ty, r * 0.32, 0, Math.PI * 2);
      boardCtx.fillStyle = '#FFE090';
      boardCtx.fill();
      boardCtx.restore();
    }

    // Active tokens traversing the 15x15 board
    const [rtx, rty] = cellCenter(4, 6);
    draw3DToken(rtx, rty, cellSize * 0.42, '#FF4433'); // Red token on track

    const [gtx, gty] = cellCenter(8, 3);
    draw3DToken(gtx, gty, cellSize * 0.42, '#00D878'); // Green token on track

    const [ytx, yty] = cellCenter(11, 8);
    draw3DToken(ytx, yty, cellSize * 0.42, '#FFCA28'); // Yellow token on track

    const [btx, bty] = cellCenter(7, 11);
    draw3DToken(btx, bty, cellSize * 0.42, '#3884FF'); // Blue token in home path

    // 8. SIDE ANNOTATION BADGES ON HORIZONTAL SPACE
    if (w > boardSize + 140) {
      boardCtx.save();
      boardCtx.fillStyle = '#FFE090';
      boardCtx.font = '700 11px Cinzel, serif';
      boardCtx.textAlign = 'left';
      boardCtx.fillText('🇳🇵 KATHMANDU THEME', ox + 10, oy - 4 > 14 ? oy - 6 : 18);
      boardCtx.textAlign = 'right';
      boardCtx.fillText('15×15 TOURNAMENT BOARD', ox + boardSize - 10, oy - 4 > 14 ? oy - 6 : 18);
      boardCtx.restore();
    }
  }

  // ==========================================
  // 4. HIMALAYAN SINGING BOWL AUDIO SYNTHESIZER
  // ==========================================
  const vibeBtn = document.getElementById('vibeAudioBtn');
  const vibeLabel = document.getElementById('vibeAudioLabel');
  let audioCtx = null;

  function playSingingBowl() {
    if (!audioCtx) {
      audioCtx = new (window.AudioContext || window.webkitAudioContext)();
    }
    if (audioCtx.state === 'suspended') {
      audioCtx.resume();
    }

    const t = audioCtx.currentTime;
    // Harmonic series: Fundamental 432 Hz, Octave 864 Hz, Fifth 1296 Hz
    const freqs = [432, 864, 1296, 2160];
    const gains = [0.45, 0.25, 0.15, 0.06];

    freqs.forEach((freq, idx) => {
      const osc = audioCtx.createOscillator();
      const gain = audioCtx.createGain();

      osc.type = 'sine';
      osc.frequency.setValueAtTime(freq, t);
      // Subtle pitch drift simulating physical hammered metal bowl
      osc.frequency.exponentialRampToValueAtTime(freq - 1.5, t + 4.5);

      gain.gain.setValueAtTime(gains[idx], t);
      gain.gain.exponentialRampToValueAtTime(0.0001, t + 4.5);

      osc.connect(gain);
      gain.connect(audioCtx.destination);

      osc.start(t);
      osc.stop(t + 4.5);
    });

    vibeLabel.textContent = 'Playing Chime... (Resonant)';
    setTimeout(() => {
      vibeLabel.textContent = 'Play Himalayan Temple Chime';
    }, 4500);
  }

  if (vibeBtn) vibeBtn.addEventListener('click', playSingingBowl);

  // ==========================================
  // 5. POLICY & COMPLIANCE HUB CONTROLLER
  // ==========================================
  const policyTabBtns = document.querySelectorAll('.policy-tab-btn');
  const policyPanes = document.querySelectorAll('.policy-pane');

  policyTabBtns.forEach(btn => {
    btn.addEventListener('click', () => {
      const targetPaneId = btn.getAttribute('data-policy-tab');
      policyTabBtns.forEach(b => b.classList.remove('active'));
      policyPanes.forEach(p => p.classList.remove('active'));

      btn.classList.add('active');
      const target = document.getElementById(targetPaneId);
      if (target) target.classList.add('active');
    });
  });

  const policySearch = document.getElementById('policySearchInput');
  if (policySearch) {
    policySearch.addEventListener('input', (e) => {
      const query = e.target.value.toLowerCase().trim();
      const activePane = document.querySelector('.policy-pane.active');
      if (!activePane) return;

      const articles = activePane.querySelectorAll('.policy-article');
      articles.forEach(art => {
        const text = art.textContent.toLowerCase();
        art.style.display = (!query || text.includes(query)) ? 'flex' : 'none';
      });
    });
  }

  // Initial Boot
  renderDice();
  renderSnake();
  renderKathmanduBoard();
})();

