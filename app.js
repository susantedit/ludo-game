// Ludora Interactive Web Engine & Legal Hub Controller
// Pure Vanilla JavaScript & HTML5 Canvas

(function () {
  'use strict';

  // ==========================================================================
  // 1. PROCEDURAL HIMALAYAN SINGING BOWL CHIME SYNTHESIZER (432 Hz)
  // ==========================================================================
  let audioCtx = null;

  function playTempleChime() {
    try {
      const AudioContext = window.AudioContext || window.webkitAudioContext;
      if (!AudioContext) return;
      if (!audioCtx) audioCtx = new AudioContext();
      if (audioCtx.state === 'suspended') audioCtx.resume();

      const now = audioCtx.currentTime;
      // Sacred 432 Hz overtone series
      const frequencies = [432, 864, 1296, 1728];
      const gains = [0.45, 0.22, 0.12, 0.06];
      const decays = [4.2, 3.2, 2.4, 1.8];

      frequencies.forEach((freq, idx) => {
        const osc = audioCtx.createOscillator();
        const gainNode = audioCtx.createGain();

        osc.type = 'sine';
        osc.frequency.setValueAtTime(freq, now);

        gainNode.gain.setValueAtTime(0, now);
        gainNode.gain.linearRampToValueAtTime(gains[idx], now + 0.04);
        gainNode.gain.exponentialRampToValueAtTime(0.0001, now + decays[idx]);

        osc.connect(gainNode);
        gainNode.connect(audioCtx.destination);

        osc.start(now);
        osc.stop(now + decays[idx] + 0.1);
      });

      const audioBtnLabel = document.getElementById('navAudioLabel');
      if (audioBtnLabel) {
        audioBtnLabel.textContent = 'Harmonic Chime Ringing...';
        setTimeout(() => {
          audioBtnLabel.textContent = 'Himalayan Chime (432Hz)';
        }, 3000);
      }
    } catch (e) {
      console.warn('Audio not available:', e);
    }
  }

  const navAudioBtn = document.getElementById('navAudioBtn');
  if (navAudioBtn) navAudioBtn.addEventListener('click', playTempleChime);

  // ==========================================================================
  // 2. REAL 3D ISOMETRIC TUMBLING DICE RENDERER
  // ==========================================================================
  const diceCanvas = document.getElementById('diceCanvas');
  if (diceCanvas) {
    const diceCtx = diceCanvas.getContext('2d');
    const dicePill = document.getElementById('diceValuePill');
    const rollBtn = document.getElementById('rollDiceBtn');

    const CUBE_VERTICES = [
      [-1, -1, -1], [1, -1, -1], [1, 1, -1], [-1, 1, -1],
      [-1, -1, 1], [1, -1, 1], [1, 1, 1], [-1, 1, 1]
    ];

    const CUBE_FACES = [
      { value: 1, normal: [0, 0, 1], indices: [4, 5, 6, 7] },
      { value: 6, normal: [0, 0, -1], indices: [1, 0, 3, 2] },
      { value: 2, normal: [0, 1, 0], indices: [7, 6, 2, 3] },
      { value: 5, normal: [0, -1, 0], indices: [4, 0, 1, 5] },
      { value: 3, normal: [1, 0, 0], indices: [5, 1, 2, 6] },
      { value: 4, normal: [-1, 0, 0], indices: [0, 4, 7, 3] }
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
      if (dicePill) dicePill.textContent = 'Rolling...';
    }

    if (rollBtn) rollBtn.addEventListener('click', rollDice);
    window.addEventListener('keydown', (e) => {
      if (e.code === 'Space' && e.target === document.body) {
        e.preventDefault();
        rollDice();
      }
    });

    function drawPip(cx, cy, radius, isSix) {
      diceCtx.save();
      const pipGrad = diceCtx.createRadialGradient(cx - radius * 0.3, cy - radius * 0.3, radius * 0.1, cx, cy, radius);
      if (isSix) {
        pipGrad.addColorStop(0, '#FF6B6B');
        pipGrad.addColorStop(1, '#C23B22');
      } else {
        pipGrad.addColorStop(0, '#FFE090');
        pipGrad.addColorStop(0.7, '#D49B28');
        pipGrad.addColorStop(1, '#66470C');
      }
      diceCtx.fillStyle = pipGrad;
      diceCtx.beginPath();
      diceCtx.arc(cx, cy, radius, 0, Math.PI * 2);
      diceCtx.fill();
      diceCtx.restore();
    }

    function drawPipsOnFace(pts, value) {
      const p0 = pts[0], p1 = pts[1], p2 = pts[2], p3 = pts[3];
      const pipRadius = 4.5;
      const getPos = (u, v) => [
        (1 - u) * (1 - v) * p0[0] + u * (1 - v) * p1[0] + u * v * p2[0] + (1 - u) * v * p3[0],
        (1 - u) * (1 - v) * p0[1] + u * (1 - v) * p1[1] + u * v * p2[1] + (1 - u) * v * p3[1]
      ];

      const isSix = (value === 6);
      if (value === 1 || value === 3 || value === 5) {
        const c = getPos(0.5, 0.5);
        drawPip(c[0], c[1], pipRadius * 1.3, isSix);
      }
      if (value >= 2) {
        const c1 = getPos(0.25, 0.25), c2 = getPos(0.75, 0.75);
        drawPip(c1[0], c1[1], pipRadius, isSix);
        drawPip(c2[0], c2[1], pipRadius, isSix);
      }
      if (value >= 4) {
        const c3 = getPos(0.75, 0.25), c4 = getPos(0.25, 0.75);
        drawPip(c3[0], c3[1], pipRadius, isSix);
        drawPip(c4[0], c4[1], pipRadius, isSix);
      }
      if (value === 6) {
        const c5 = getPos(0.25, 0.5), c6 = getPos(0.75, 0.5);
        drawPip(c5[0], c5[1], pipRadius, isSix);
        drawPip(c6[0], c6[1], pipRadius, isSix);
      }
    }

    function renderDice() {
      diceCtx.clearRect(0, 0, diceCanvas.width, diceCanvas.height);
      const cx = diceCanvas.width / 2;
      const cy = diceCanvas.height / 2 + 10;
      const size = 68;

      if (isRolling) {
        const deltaR = matMul(rotZMat(velZ), matMul(rotYMat(velY), rotXMat(velX)));
        currentDiceMatrix = matMul(deltaR, currentDiceMatrix);
        velX *= 0.96;
        velY *= 0.96;
        velZ *= 0.96;
        altitude += altVel;
        altVel -= 1.4;
        if (altitude <= 0) {
          altitude = 0;
          altVel = -altVel * 0.58;
          if (Math.abs(altVel) < 1 && Math.abs(velX) < 0.05 && Math.abs(velY) < 0.05) {
            isRolling = false;
            // Snap to exact top-face orientation for the rolled number
            currentDiceMatrix = getTargetMatrix(currentDiceFace);
            if (dicePill) dicePill.textContent = `Rolled: ${currentDiceFace} (On Top)`;
          }
        }
      }

      // Ground shadow
      const shadowW = Math.max(10, size * 1.4 - altitude * 0.4);
      const shadowH = Math.max(4, size * 0.5 - altitude * 0.15);
      const shadowGrad = diceCtx.createRadialGradient(cx, cy + size + 10, 0, cx, cy + size + 10, shadowW);
      shadowGrad.addColorStop(0, 'rgba(0, 0, 0, 0.6)');
      shadowGrad.addColorStop(1, 'transparent');
      diceCtx.fillStyle = shadowGrad;
      diceCtx.beginPath();
      diceCtx.ellipse(cx, cy + size + 10, shadowW, shadowH, 0, 0, Math.PI * 2);
      diceCtx.fill();

      const renderCy = cy - altitude;
      const rotatedVertices = CUBE_VERTICES.map(v => matMulVec(currentDiceMatrix, v));

      const facesToDraw = [];
      CUBE_FACES.forEach(face => {
        const normRot = matMulVec(currentDiceMatrix, face.normal);
        if (normRot[2] > 0.04) {
          const depth = (rotatedVertices[face.indices[0]][2] +
            rotatedVertices[face.indices[1]][2] +
            rotatedVertices[face.indices[2]][2] +
            rotatedVertices[face.indices[3]][2]) / 4;
          facesToDraw.push({ face, normRot, depth });
        }
      });

      facesToDraw.sort((a, b) => a.depth - b.depth);

      facesToDraw.forEach(({ face, normRot }) => {
        const pts = face.indices.map(idx => project(rotatedVertices[idx], size, cx, renderCy));
        const lightDir = [-0.5, 0.7, 0.5];
        const dot = Math.max(0, normRot[0] * lightDir[0] + normRot[1] * lightDir[1] + normRot[2] * lightDir[2]);
        const brightness = Math.floor(180 + dot * 75);

        diceCtx.save();
        diceCtx.beginPath();
        diceCtx.moveTo(pts[0][0], pts[0][1]);
        for (let i = 1; i < pts.length; i++) diceCtx.lineTo(pts[i][0], pts[i][1]);
        diceCtx.closePath();

        diceCtx.fillStyle = `rgb(${brightness}, ${brightness - 5}, ${brightness - 12})`;
        diceCtx.shadowColor = 'rgba(0, 0, 0, 0.4)';
        diceCtx.shadowBlur = 8;
        diceCtx.fill();

        diceCtx.strokeStyle = 'rgba(255, 255, 255, 0.6)';
        diceCtx.lineWidth = 1.5;
        diceCtx.stroke();
        diceCtx.restore();

        drawPipsOnFace(pts, face.value);
      });

      requestAnimationFrame(renderDice);
    }

    renderDice();
  }

  // ==========================================================================
  // 3. REAL 3D ANIMATED VIPER ENGINE (BÉZIER UNDULATION)
  // ==========================================================================
  const snakeCanvas = document.getElementById('snakeCanvas');
  if (snakeCanvas) {
    const snakeCtx = snakeCanvas.getContext('2d');
    const swallowBtn = document.getElementById('swallowTokenBtn');

    let animTime = 0;
    let swallowBulgeProgress = -1;

    function triggerSwallow() {
      swallowBulgeProgress = 0;
    }

    if (swallowBtn) swallowBtn.addEventListener('click', triggerSwallow);

    function evaluateBezier(p0, p1, p2, p3, t) {
      const u = 1 - t;
      const tt = t * t, uu = u * u;
      const uuu = uu * u, ttt = tt * t;
      return [
        uuu * p0[0] + 3 * uu * t * p1[0] + 3 * u * tt * p2[0] + ttt * p3[0],
        uuu * p0[1] + 3 * uu * t * p1[1] + 3 * u * tt * p2[1] + ttt * p3[1]
      ];
    }

    function evaluateBezierDerivative(p0, p1, p2, p3, t) {
      const u = 1 - t;
      return [
        3 * u * u * (p1[0] - p0[0]) + 6 * u * t * (p2[0] - p1[0]) + 3 * t * t * (p3[0] - p2[0]),
        3 * u * u * (p1[1] - p0[1]) + 6 * u * t * (p2[1] - p1[1]) + 3 * t * t * (p3[1] - p2[1])
      ];
    }

    function renderSnake() {
      snakeCtx.clearRect(0, 0, snakeCanvas.width, snakeCanvas.height);
      animTime += 0.04;

      if (swallowBulgeProgress >= 0) {
        swallowBulgeProgress += 0.008;
        if (swallowBulgeProgress > 1.2) swallowBulgeProgress = -1;
      }

      const p0 = [70, 310];
      const p1 = [320, 240];
      const p2 = [80, 110];
      const p3 = [290, 45];

      const numSegments = 90;
      const spinePoints = [];
      const normals = [];

      for (let i = 0; i <= numSegments; i++) {
        const s = i / numSegments;
        const [bx, by] = evaluateBezier(p0, p1, p2, p3, s);
        const [dx, dy] = evaluateBezierDerivative(p0, p1, p2, p3, s);
        const len = Math.hypot(dx, dy) || 1;
        const nx = -dy / len;
        const ny = dx / len;

        const wave = Math.sin(s * 10 - animTime * 4) * (14 * (1 - s * 0.4));
        const px = bx + nx * wave;
        const py = by + ny * wave;

        spinePoints.push([px, py]);
        normals.push([nx, ny]);
      }

      // Draw shadow
      snakeCtx.save();
      snakeCtx.beginPath();
      for (let i = 0; i < spinePoints.length; i++) {
        const [x, y] = spinePoints[i];
        if (i === 0) snakeCtx.moveTo(x + 10, y + 14);
        else snakeCtx.lineTo(x + 10, y + 14);
      }
      snakeCtx.strokeStyle = 'rgba(0, 0, 0, 0.45)';
      snakeCtx.lineWidth = 22;
      snakeCtx.lineCap = 'round';
      snakeCtx.filter = 'blur(6px)';
      snakeCtx.stroke();
      snakeCtx.restore();

      // Render Body Segments
      for (let i = 0; i < spinePoints.length - 1; i++) {
        const s = i / numSegments;
        let baseRadius = (s < 0.85) ? 14 * Math.sin(s * Math.PI * 0.6 + 0.3) : 10 * (1 - s) * 6;
        if (swallowBulgeProgress >= 0) {
          const dist = Math.abs((1 - s) - swallowBulgeProgress);
          if (dist < 0.12) {
            baseRadius += (1 - dist / 0.12) * 11;
          }
        }

        const [x1, y1] = spinePoints[i];
        const [x2, y2] = spinePoints[i + 1];
        const [nx, ny] = normals[i];

        const grad = snakeCtx.createLinearGradient(x1 - nx * baseRadius, y1 - ny * baseRadius, x1 + nx * baseRadius, y1 + ny * baseRadius);
        grad.addColorStop(0, '#004724');
        grad.addColorStop(0.3, '#00C875');
        grad.addColorStop(0.6, '#FFDF85');
        grad.addColorStop(1, '#002B14');

        snakeCtx.save();
        snakeCtx.beginPath();
        snakeCtx.moveTo(x1 - nx * baseRadius, y1 - ny * baseRadius);
        snakeCtx.lineTo(x2 - nx * baseRadius, y2 - ny * baseRadius);
        snakeCtx.lineTo(x2 + nx * baseRadius, y2 + ny * baseRadius);
        snakeCtx.lineTo(x1 + nx * baseRadius, y1 + ny * baseRadius);
        snakeCtx.closePath();
        snakeCtx.fillStyle = grad;
        snakeCtx.fill();

        // Scale Diamond Pattern
        if (i % 3 === 0) {
          snakeCtx.fillStyle = 'rgba(255, 223, 133, 0.35)';
          snakeCtx.beginPath();
          snakeCtx.arc(x1, y1, baseRadius * 0.35, 0, Math.PI * 2);
          snakeCtx.fill();
        }
        snakeCtx.restore();
      }

      // Draw Viper Head
      const headIdx = spinePoints.length - 1;
      const [hx, hy] = spinePoints[headIdx];
      const [hdx, hdy] = evaluateBezierDerivative(p0, p1, p2, p3, 1);
      const headAngle = Math.atan2(hdy, hdx);

      snakeCtx.save();
      snakeCtx.translate(hx, hy);
      snakeCtx.rotate(headAngle);

      // Flicking Forked Tongue
      const tongueExtension = Math.max(0, Math.sin(animTime * 6)) * 16;
      if (tongueExtension > 2) {
        snakeCtx.strokeStyle = '#FF3344';
        snakeCtx.lineWidth = 2;
        snakeCtx.beginPath();
        snakeCtx.moveTo(14, 0);
        snakeCtx.lineTo(14 + tongueExtension, 0);
        snakeCtx.lineTo(14 + tongueExtension + 5, -4);
        snakeCtx.moveTo(14 + tongueExtension, 0);
        snakeCtx.lineTo(14 + tongueExtension + 5, 4);
        snakeCtx.stroke();
      }

      // Viper Head Wedge
      snakeCtx.fillStyle = '#00582F';
      snakeCtx.beginPath();
      snakeCtx.moveTo(16, 0);
      snakeCtx.lineTo(-12, -14);
      snakeCtx.lineTo(-18, 0);
      snakeCtx.lineTo(-12, 14);
      snakeCtx.closePath();
      snakeCtx.shadowColor = 'rgba(0,0,0,0.5)';
      snakeCtx.shadowBlur = 6;
      snakeCtx.fill();

      // Predatory Eyes
      const drawEye = (ey) => {
        snakeCtx.fillStyle = '#FFAA00';
        snakeCtx.beginPath();
        snakeCtx.arc(0, ey, 3.5, 0, Math.PI * 2);
        snakeCtx.fill();
        snakeCtx.fillStyle = '#000000';
        snakeCtx.beginPath();
        snakeCtx.ellipse(0.5, ey, 1, 3, 0, 0, Math.PI * 2);
        snakeCtx.fill();
      };
      drawEye(-8);
      drawEye(8);

      snakeCtx.restore();

      requestAnimationFrame(renderSnake);
    }

    renderSnake();
  }

  // ==========================================================================
  // 4. KATHMANDU SACRED MANDALA BOARD RENDERER
  // ==========================================================================
  const boardCanvas = document.getElementById('boardCanvas');
  if (boardCanvas) {
    const bCtx = boardCanvas.getContext('2d');

    function renderKathmanduBoard() {
      const w = boardCanvas.width;
      const h = boardCanvas.height;
      bCtx.clearRect(0, 0, w, h);

      const pad = 12;
      const boardSize = Math.min(w - pad * 2, h - pad * 2);
      const ox = (w - boardSize) / 2;
      const oy = (h - boardSize) / 2;

      const borderThick = boardSize * 0.042;
      const innerSize = boardSize - borderThick * 2;
      const gridOx = ox + borderThick;
      const gridOy = oy + borderThick;
      const cellSize = innerSize / 15;

      const cellX = (c) => gridOx + c * cellSize;
      const cellY = (r) => gridOy + r * cellSize;
      const cellCenter = (c, r) => [cellX(c) + cellSize / 2, cellY(r) + cellSize / 2];

      // 1. CARVED DARK WALNUT WOOD FRAME
      bCtx.save();
      const woodGrad = bCtx.createLinearGradient(ox, oy, ox + boardSize, oy + boardSize);
      woodGrad.addColorStop(0, '#1E130B');
      woodGrad.addColorStop(0.3, '#2A1A0F');
      woodGrad.addColorStop(0.7, '#1E130B');
      woodGrad.addColorStop(1, '#160E08');
      bCtx.fillStyle = woodGrad;
      bCtx.fillRect(ox, oy, boardSize, boardSize);

      bCtx.strokeStyle = 'rgba(255, 255, 255, 0.04)';
      bCtx.lineWidth = 1;
      for (let i = 4; i < boardSize; i += 10) {
        bCtx.beginPath();
        bCtx.moveTo(ox + i, oy);
        bCtx.lineTo(ox + i, oy + boardSize);
        bCtx.stroke();
      }

      bCtx.strokeStyle = '#D4AF37';
      bCtx.lineWidth = 2.5;
      bCtx.strokeRect(ox, oy, boardSize, boardSize);
      bCtx.strokeRect(gridOx - 1, gridOy - 1, innerSize + 2, innerSize + 2);
      bCtx.restore();

      // 2. ORNATE NEWARI BRASS CORNERS & PRAYER FLAG TASSELS
      const flagColors = ['#0066CC', '#EEEEEE', '#CC2222', '#008844', '#FFCC00'];
      const corners = [
        [ox + 4, oy + 4, 1, 1],
        [ox + boardSize - 4, oy + 4, -1, 1],
        [ox + boardSize - 4, oy + boardSize - 4, -1, -1],
        [ox + 4, oy + boardSize - 4, 1, -1]
      ];

      corners.forEach(([kx, ky, dx, dy]) => {
        bCtx.save();
        bCtx.strokeStyle = '#FFE090';
        bCtx.lineWidth = 2;
        bCtx.beginPath();
        bCtx.moveTo(kx, ky + dy * 18);
        bCtx.lineTo(kx, ky);
        bCtx.lineTo(kx + dx * 18, ky);
        bCtx.stroke();
        bCtx.restore();

        flagColors.forEach((col, idx) => {
          bCtx.save();
          bCtx.beginPath();
          const tLen = 14 + idx * 3.5;
          const angle = (idx - 2) * 0.18 + (dx < 0 ? Math.PI : 0);
          bCtx.moveTo(kx, ky);
          bCtx.lineTo(kx + Math.cos(angle) * tLen, ky + Math.sin(angle) * tLen);
          bCtx.strokeStyle = col;
          bCtx.lineWidth = 2.4;
          bCtx.lineCap = 'round';
          bCtx.stroke();
          bCtx.restore();
        });
      });

      // 3. DRAW FOUR 6x6 COURTYARD YARDS (CHOWKS)
      function drawYard(startCol, startRow, colorHex, accentHex, labelText, palaceName) {
        const x = cellX(startCol);
        const y = cellY(startRow);
        const yardWidth = cellSize * 6;

        bCtx.save();
        const yardGrad = bCtx.createRadialGradient(x + yardWidth/2, y + yardWidth/2, 10, x + yardWidth/2, y + yardWidth/2, yardWidth/2);
        yardGrad.addColorStop(0, colorHex);
        yardGrad.addColorStop(1, '#0C0F17');
        bCtx.fillStyle = yardGrad;
        bCtx.fillRect(x, y, yardWidth, yardWidth);

        bCtx.strokeStyle = accentHex;
        bCtx.lineWidth = 2;
        bCtx.strokeRect(x + 1, y + 1, yardWidth - 2, yardWidth - 2);

        const innerMargin = cellSize * 0.85;
        const innerW = yardWidth - innerMargin * 2;
        bCtx.fillStyle = '#141824';
        bCtx.fillRect(x + innerMargin, y + innerMargin, innerW, innerW);
        bCtx.strokeStyle = '#D4AF37';
        bCtx.lineWidth = 1.5;
        bCtx.strokeRect(x + innerMargin, y + innerMargin, innerW, innerW);

        bCtx.fillStyle = 'rgba(255, 224, 144, 0.75)';
        bCtx.font = `600 ${Math.max(9, cellSize * 0.32)}px Cinzel, serif`;
        bCtx.textAlign = 'center';
        bCtx.fillText(palaceName, x + yardWidth / 2, y + innerMargin * 0.65);

        const slotOffsets = [
          [x + cellSize * 2.0, y + cellSize * 2.0],
          [x + cellSize * 4.0, y + cellSize * 2.0],
          [x + cellSize * 2.0, y + cellSize * 4.0],
          [x + cellSize * 4.0, y + cellSize * 4.0]
        ];

        slotOffsets.forEach(([sx, sy], sIdx) => {
          bCtx.save();
          bCtx.beginPath();
          bCtx.arc(sx, sy, cellSize * 0.65, 0, Math.PI * 2);
          bCtx.fillStyle = '#080B11';
          bCtx.fill();
          bCtx.strokeStyle = accentHex;
          bCtx.lineWidth = 1.8;
          bCtx.stroke();

          bCtx.beginPath();
          bCtx.arc(sx, sy, cellSize * 0.45, 0, Math.PI * 2);
          bCtx.strokeStyle = 'rgba(212, 175, 55, 0.45)';
          bCtx.stroke();

          if (sIdx < 2) {
            draw3DToken(sx, sy, cellSize * 0.42, colorHex);
          }
          bCtx.restore();
        });

        bCtx.restore();
      }

      drawYard(0, 0, '#9E2A1A', '#FF7D6B', 'RED', 'Patan Chowk');
      drawYard(9, 0, '#0E5C38', '#5AE49A', 'GREEN', 'Bhaktapur');
      drawYard(9, 9, '#A6730A', '#FFDF75', 'YELLOW', 'Basantapur');
      drawYard(0, 9, '#154182', '#7AA8FF', 'BLUE', 'Kirtipur');

      // 4. DRAW 72 CROSS TRACK CELLS
      function getCellInfo(c, r) {
        if ((c < 6 && r < 6) || (c > 8 && r < 6) || (c < 6 && r > 8) || (c > 8 && r > 8)) {
          return { isTrack: false };
        }
        if (c >= 6 && c <= 8 && r >= 6 && r <= 8) {
          return { isCenter: true, isTrack: false };
        }

        if (c === 7 && r >= 1 && r <= 5) return { isTrack: true, isHomePath: true, color: '#0E5C38', arrow: '↓' };
        if (c === 7 && r >= 9 && r <= 13) return { isTrack: true, isHomePath: true, color: '#154182', arrow: '↑' };
        if (r === 7 && c >= 1 && c <= 5) return { isTrack: true, isHomePath: true, color: '#9E2A1A', arrow: '→' };
        if (r === 7 && c >= 9 && c <= 13) return { isTrack: true, isHomePath: true, color: '#A6730A', arrow: '←' };

        const starCells = [
          [1, 6], [6, 2], [8, 1], [12, 6], [13, 8], [8, 12], [6, 13], [2, 8]
        ];
        const isStar = starCells.some(([sc, sr]) => sc === c && sr === r);

        let startColor = null;
        if (c === 1 && r === 6) startColor = '#9E2A1A';
        if (c === 8 && r === 1) startColor = '#0E5C38';
        if (c === 13 && r === 8) startColor = '#A6730A';
        if (c === 6 && r === 13) startColor = '#154182';

        return { isTrack: true, isStar, isStart: !!startColor, startColor };
      }

      for (let r = 0; r < 15; r++) {
        for (let c = 0; c < 15; c++) {
          const info = getCellInfo(c, r);
          if (!info.isTrack) continue;

          const x = cellX(c);
          const y = cellY(r);

          bCtx.save();
          if (info.isHomePath) {
            bCtx.fillStyle = info.color;
          } else if (info.isStart) {
            bCtx.fillStyle = info.startColor;
          } else {
            bCtx.fillStyle = (r + c) % 2 === 0 ? '#1C212E' : '#141822';
          }
          bCtx.fillRect(x, y, cellSize, cellSize);

          bCtx.strokeStyle = 'rgba(212, 175, 55, 0.4)';
          bCtx.lineWidth = 1;
          bCtx.strokeRect(x, y, cellSize, cellSize);

          if (info.isHomePath && info.arrow) {
            bCtx.fillStyle = 'rgba(255, 224, 144, 0.85)';
            bCtx.font = `700 ${cellSize * 0.45}px sans-serif`;
            bCtx.textAlign = 'center';
            bCtx.textBaseline = 'middle';
            bCtx.fillText(info.arrow, x + cellSize / 2, y + cellSize / 2 + 1);
          }

          if (info.isStar) {
            drawNepaliMandalaStar(x + cellSize / 2, y + cellSize / 2, cellSize * 0.38);
          }

          bCtx.restore();
        }
      }

      function drawNepaliMandalaStar(cx, cy, r) {
        bCtx.save();
        bCtx.fillStyle = '#FFE090';
        bCtx.strokeStyle = '#D4AF37';
        bCtx.lineWidth = 1.2;
        bCtx.beginPath();
        for (let i = 0; i < 16; i++) {
          const rad = (i % 2 === 0) ? r : r * 0.45;
          const ang = (i * Math.PI) / 8 - Math.PI / 2;
          const px = cx + Math.cos(ang) * rad;
          const py = cy + Math.sin(ang) * rad;
          if (i === 0) bCtx.moveTo(px, py);
          else bCtx.lineTo(px, py);
        }
        bCtx.closePath();
        bCtx.fill();
        bCtx.stroke();
        bCtx.restore();
      }

      // 5. DRAW CENTRAL 3x3 HOME TRIANGLES
      const goalX = cellX(6);
      const goalY = cellY(6);
      const goalW = cellSize * 3;
      const centerPt = [goalX + goalW / 2, goalY + goalW / 2];

      const homeTriangles = [
        { p1: [goalX, goalY], p2: [goalX, goalY + goalW], color: '#9E2A1A' },
        { p1: [goalX, goalY], p2: [goalX + goalW, goalY], color: '#0E5C38' },
        { p1: [goalX + goalW, goalY], p2: [goalX + goalW, goalY + goalW], color: '#A6730A' },
        { p1: [goalX, goalY + goalW], p2: [goalX + goalW, goalY + goalW], color: '#154182' }
      ];

      homeTriangles.forEach(tri => {
        bCtx.save();
        bCtx.beginPath();
        bCtx.moveTo(centerPt[0], centerPt[1]);
        bCtx.lineTo(tri.p1[0], tri.p1[1]);
        bCtx.lineTo(tri.p2[0], tri.p2[1]);
        bCtx.closePath();
        bCtx.fillStyle = tri.color;
        bCtx.fill();
        bCtx.strokeStyle = 'rgba(212, 175, 55, 0.6)';
        bCtx.lineWidth = 1.5;
        bCtx.stroke();
        bCtx.restore();
      });

      // 6. CENTRAL SWAYAMBHUNATH WISDOM EYES MEDALLION
      const medR = cellSize * 1.35;
      const [mcx, mcy] = centerPt;

      bCtx.save();
      for (let p = 0; p < 16; p++) {
        const pAng = (p * Math.PI * 2) / 16;
        const px = mcx + Math.cos(pAng) * (medR + 4);
        const py = mcy + Math.sin(pAng) * (medR + 4);
        bCtx.save();
        bCtx.translate(px, py);
        bCtx.rotate(pAng + Math.PI / 2);
        bCtx.fillStyle = (p % 2 === 0) ? '#D4AF37' : '#9E2A1A';
        bCtx.beginPath();
        bCtx.ellipse(0, 0, 3.5, 8, 0, 0, Math.PI * 2);
        bCtx.fill();
        bCtx.restore();
      }

      const goldGrad = bCtx.createRadialGradient(mcx - medR * 0.3, mcy - medR * 0.3, 4, mcx, mcy, medR);
      goldGrad.addColorStop(0, '#FFF1B8');
      goldGrad.addColorStop(0.55, '#D4AF37');
      goldGrad.addColorStop(1, '#7A5B0B');
      bCtx.fillStyle = goldGrad;
      bCtx.beginPath();
      bCtx.arc(mcx, mcy, medR, 0, Math.PI * 2);
      bCtx.fill();
      bCtx.strokeStyle = '#2B1C03';
      bCtx.lineWidth = 2.5;
      bCtx.stroke();

      const eyeSpacing = medR * 0.38;
      const eyeElevation = medR * 0.12;

      function drawWisdomEye(ex) {
        bCtx.save();
        bCtx.beginPath();
        bCtx.moveTo(ex - 12, mcy - eyeElevation);
        bCtx.quadraticCurveTo(ex, mcy - eyeElevation - 9, ex + 12, mcy - eyeElevation);
        bCtx.quadraticCurveTo(ex, mcy - eyeElevation + 7, ex - 12, mcy - eyeElevation);
        bCtx.fillStyle = '#FFFFFF';
        bCtx.fill();
        bCtx.strokeStyle = '#0B0E14';
        bCtx.lineWidth = 1.8;
        bCtx.stroke();

        bCtx.beginPath();
        bCtx.arc(ex, mcy - eyeElevation - 0.5, 4.2, 0, Math.PI * 2);
        bCtx.fillStyle = '#0F2C59';
        bCtx.fill();

        bCtx.beginPath();
        bCtx.moveTo(ex - 14, mcy - eyeElevation - 6);
        bCtx.quadraticCurveTo(ex, mcy - eyeElevation - 14, ex + 14, mcy - eyeElevation - 5);
        bCtx.strokeStyle = '#0B0E14';
        bCtx.lineWidth = 2;
        bCtx.stroke();
        bCtx.restore();
      }

      drawWisdomEye(mcx - eyeSpacing);
      drawWisdomEye(mcx + eyeSpacing);

      bCtx.beginPath();
      bCtx.moveTo(mcx, mcy + medR * 0.05);
      bCtx.lineTo(mcx, mcy + medR * 0.22);
      bCtx.quadraticCurveTo(mcx + 7, mcy + medR * 0.32, mcx, mcy + medR * 0.42);
      bCtx.strokeStyle = '#0B0E14';
      bCtx.lineWidth = 2.2;
      bCtx.stroke();

      bCtx.beginPath();
      bCtx.arc(mcx, mcy - medR * 0.32, 2.5, 0, Math.PI * 2);
      bCtx.fillStyle = '#CC2222';
      bCtx.fill();
      bCtx.restore();

      // 7. REAL TACTILE 3D TOKENS ON ACTIVE TRACK
      function draw3DToken(tx, ty, r, colHex) {
        bCtx.save();
        bCtx.beginPath();
        bCtx.ellipse(tx + 2, ty + 3, r * 1.05, r * 0.55, 0, 0, Math.PI * 2);
        bCtx.fillStyle = 'rgba(0, 0, 0, 0.45)';
        bCtx.filter = 'blur(3px)';
        bCtx.fill();
        bCtx.filter = 'none';

        const tokenGrad = bCtx.createRadialGradient(tx - r * 0.3, ty - r * 0.35, r * 0.1, tx, ty, r);
        tokenGrad.addColorStop(0, '#FFFFFF');
        tokenGrad.addColorStop(0.25, colHex);
        tokenGrad.addColorStop(1, '#0B0E14');
        bCtx.beginPath();
        bCtx.arc(tx, ty, r, 0, Math.PI * 2);
        bCtx.fillStyle = tokenGrad;
        bCtx.fill();
        bCtx.strokeStyle = '#D4AF37';
        bCtx.lineWidth = 1.4;
        bCtx.stroke();

        bCtx.beginPath();
        bCtx.arc(tx, ty, r * 0.32, 0, Math.PI * 2);
        bCtx.fillStyle = '#FFE090';
        bCtx.fill();
        bCtx.restore();
      }

      const [rtx, rty] = cellCenter(4, 6);
      draw3DToken(rtx, rty, cellSize * 0.42, '#FF4433');

      const [gtx, gty] = cellCenter(8, 3);
      draw3DToken(gtx, gty, cellSize * 0.42, '#00D878');

      const [ytx, yty] = cellCenter(11, 8);
      draw3DToken(ytx, yty, cellSize * 0.42, '#FFCA28');

      const [btx, bty] = cellCenter(7, 11);
      draw3DToken(btx, bty, cellSize * 0.42, '#3884FF');

      if (w > boardSize + 140) {
        bCtx.save();
        bCtx.fillStyle = '#FFE090';
        bCtx.font = '700 11px Cinzel, serif';
        bCtx.textAlign = 'left';
        bCtx.fillText('🇳🇵 KATHMANDU THEME', ox + 10, oy - 4 > 14 ? oy - 6 : 18);
        bCtx.textAlign = 'right';
        bCtx.fillText('15×15 TOURNAMENT BOARD', ox + boardSize - 10, oy - 4 > 14 ? oy - 6 : 18);
        bCtx.restore();
      }
    }

    renderKathmanduBoard();
  }

  // ==========================================================================
  // 5. LEGAL HUB TABS & ACCESSIBILITY CONTROLLER
  // ==========================================================================
  const tabBtns = document.querySelectorAll('.tab-btn');
  const contentPanes = document.querySelectorAll('.legal-content-pane');

  tabBtns.forEach(btn => {
    btn.addEventListener('click', () => {
      const targetId = btn.getAttribute('data-tab');

      tabBtns.forEach(b => b.classList.remove('active'));
      contentPanes.forEach(p => p.classList.remove('active'));

      btn.classList.add('active');
      const targetPane = document.getElementById(targetId);
      if (targetPane) targetPane.classList.add('active');
    });
  });

  // Search Filter in Legal Hub
  const legalSearchInput = document.getElementById('legalSearchInput');
  if (legalSearchInput) {
    legalSearchInput.addEventListener('input', (e) => {
      const query = e.target.value.toLowerCase().trim();
      const activePane = document.querySelector('.legal-content-pane.active');
      if (!activePane) return;

      const articles = activePane.querySelectorAll('.legal-article');
      articles.forEach(article => {
        const text = article.textContent.toLowerCase();
        if (!query || text.includes(query)) {
          article.style.display = 'flex';
        } else {
          article.style.display = 'none';
        }
      });
    });
  }

})();
