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

  let rotX = 0.615; // Standard isometric tilt
  let rotY = 0.785;
  let rotZ = 0.0;
  let velX = 0;
  let velY = 0;
  let velZ = 0;
  let altitude = 0;
  let altVel = 0;
  let isRolling = false;
  let currentDiceFace = 6;

  function rotateVector(v, ax, ay, az) {
    let [x, y, z] = v;
    // Rotate X
    let cx = Math.cos(ax), sx = Math.sin(ax);
    let y1 = y * cx - z * sx;
    let z1 = y * sx + z * cx;
    // Rotate Y
    let cy = Math.cos(ay), sy = Math.sin(ay);
    let x2 = x * cy + z1 * sy;
    let z2 = -x * sy + z1 * cy;
    // Rotate Z
    let cz = Math.cos(az), sz = Math.sin(az);
    let x3 = x2 * cz - y1 * sz;
    let y3 = x2 * sz + y1 * cz;
    return [x3, y3, z2];
  }

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
      rotX += velX;
      rotY += velY;
      rotZ += velZ;
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
          // Snap to resting face
          snapToFace(currentDiceFace);
          dicePill.textContent = `Face: ${currentDiceFace}`;
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
    const rotVertices = CUBE_VERTICES.map(v => rotateVector(v, rotX, rotY, rotZ));
    const projVertices = rotVertices.map(v => project(v, size, cx, currentY));

    // Sort faces by depth
    const lightDir = [-0.45, -0.65, 0.6];
    const lenL = Math.sqrt(lightDir[0]**2 + lightDir[1]**2 + lightDir[2]**2);
    const nl = lightDir.map(n => n / lenL);

    const sortedFaces = CUBE_FACES.map(f => {
      const rotNorm = rotateVector(f.normal, rotX, rotY, rotZ);
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

  function snapToFace(face) {
    // Aligns orientation to face target
    const alignments = {
      1: [0.615, 0.785, 0],
      2: [-1.2, 0.785, 0],
      3: [0.615, -0.785, 0],
      4: [0.615, 2.35, 0],
      5: [1.2, 0.785, 0],
      6: [0.615, -2.35, 0]
    };
    const target = alignments[face] || [0.615, 0.785, 0];
    rotX = target[0];
    rotY = target[1];
    rotZ = target[2];
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
  const boardCanvas = document.getElementById('boardCanvas');
  const boardCtx = boardCanvas.getContext('2d');

  function renderKathmanduBoard() {
    const w = boardCanvas.width;
    const h = boardCanvas.height;
    boardCtx.clearRect(0, 0, w, h);

    const pad = 24;
    const size = Math.min(w, h) - pad * 2;
    const ox = (w - size) / 2;
    const oy = (h - size) / 2;
    const cx = ox + size / 2;
    const cy = oy + size / 2;

    // 1. Carved Dark Walnut Outer Frame
    boardCtx.save();
    boardCtx.fillStyle = '#1A120B';
    boardCtx.fillRect(ox, oy, size, size);
    boardCtx.strokeStyle = '#D4AF37';
    boardCtx.lineWidth = 3.5;
    boardCtx.strokeRect(ox, oy, size, size);

    // Inner Brass Inlay Ring
    boardCtx.strokeStyle = 'rgba(212, 175, 55, 0.4)';
    boardCtx.lineWidth = 1.5;
    boardCtx.strokeRect(ox + 14, oy + 14, size - 28, size - 28);
    boardCtx.restore();

    // 2. Stone Mandala Tile Grid
    const innerSize = size - 36;
    const startX = ox + 18;
    const startY = oy + 18;
    const tilesPerSide = 10;
    const tileSize = innerSize / tilesPerSide;

    for (let r = 0; r < tilesPerSide; r++) {
      for (let c = 0; c < tilesPerSide; c++) {
        const tx = startX + c * tileSize;
        const ty = startY + r * tileSize;
        boardCtx.save();
        boardCtx.fillStyle = (r + c) % 2 === 0 ? '#26221D' : '#1F1B16';
        boardCtx.fillRect(tx, ty, tileSize, tileSize);
        boardCtx.strokeStyle = 'rgba(255, 255, 255, 0.05)';
        boardCtx.strokeRect(tx, ty, tileSize, tileSize);

        // Ancient mandala glyph on special tiles
        if ((r * 10 + c) % 7 === 0) {
          boardCtx.beginPath();
          boardCtx.arc(tx + tileSize/2, ty + tileSize/2, tileSize * 0.22, 0, Math.PI * 2);
          boardCtx.strokeStyle = 'rgba(229, 169, 60, 0.25)';
          boardCtx.stroke();
        }
        boardCtx.restore();
      }
    }

    // 3. Central Golden Swayambhunath Wisdom Eyes Medallion
    const medR = size * 0.18;
    boardCtx.save();
    boardCtx.beginPath();
    boardCtx.arc(cx, cy, medR, 0, Math.PI * 2);

    const goldGrad = boardCtx.createRadialGradient(cx - medR*0.3, cy - medR*0.3, 5, cx, cy, medR);
    goldGrad.addColorStop(0, '#FFE899');
    goldGrad.addColorStop(0.6, '#D4AF37');
    goldGrad.addColorStop(1, '#8A6812');
    boardCtx.fillStyle = goldGrad;
    boardCtx.fill();
    boardCtx.strokeStyle = '#3D2800';
    boardCtx.lineWidth = 3;
    boardCtx.stroke();

    // Swayambhunath Wisdom Eyes
    const eyeSpacing = medR * 0.42;
    const eyeY = cy - medR * 0.12;

    function drawWisdomEye(ex, isLeft) {
      boardCtx.save();
      boardCtx.beginPath();
      // Curved upper eyelid
      boardCtx.moveTo(ex - 16, eyeY);
      boardCtx.quadraticCurveTo(ex, eyeY - 12, ex + 16, eyeY);
      // Curved lower eyelid
      boardCtx.quadraticCurveTo(ex, eyeY + 8, ex - 16, eyeY);
      boardCtx.fillStyle = '#FFFFFF';
      boardCtx.fill();
      boardCtx.strokeStyle = '#0B0E14';
      boardCtx.lineWidth = 2.2;
      boardCtx.stroke();

      // Deep Blue/Black Iris
      boardCtx.beginPath();
      boardCtx.arc(ex, eyeY - 1, 6, 0, Math.PI * 2);
      boardCtx.fillStyle = '#0E2442';
      boardCtx.fill();

      // Eyebrow
      boardCtx.beginPath();
      boardCtx.moveTo(ex - 18, eyeY - 8);
      boardCtx.quadraticCurveTo(ex, eyeY - 18, ex + 18, eyeY - 6);
      boardCtx.strokeStyle = '#0B0E14';
      boardCtx.lineWidth = 2.5;
      boardCtx.stroke();
      boardCtx.restore();
    }

    drawWisdomEye(cx - eyeSpacing, true);
    drawWisdomEye(cx + eyeSpacing, false);

    // Question-mark shaped Nose of Unity ("Ek" in Nepali)
    boardCtx.beginPath();
    boardCtx.moveTo(cx, cy + medR * 0.08);
    boardCtx.quadraticCurveTo(cx + 10, cy + medR * 0.25, cx, cy + medR * 0.38);
    boardCtx.strokeStyle = '#0B0E14';
    boardCtx.lineWidth = 2.5;
    boardCtx.stroke();

    boardCtx.restore();

    // 4. Sacred 5-Color Prayer Flag Corner Tassels
    const flagColors = ['#0066CC', '#EEEEEE', '#CC2222', '#008844', '#FFCC00'];
    const corners = [
      [ox + 6, oy + 6],
      [ox + size - 6, oy + 6],
      [ox + size - 6, oy + size - 6],
      [ox + 6, oy + size - 6]
    ];

    corners.forEach(([cornerX, cornerY]) => {
      flagColors.forEach((col, idx) => {
        boardCtx.save();
        boardCtx.beginPath();
        const tLen = 22 + idx * 4;
        const angle = (idx - 2) * 0.22;
        boardCtx.moveTo(cornerX, cornerY);
        boardCtx.lineTo(cornerX + Math.cos(angle) * tLen, cornerY + Math.sin(angle) * tLen);
        boardCtx.strokeStyle = col;
        boardCtx.lineWidth = 3;
        boardCtx.lineCap = 'round';
        boardCtx.stroke();
        boardCtx.restore();
      });
    });
  }

  // Initial Boot
  renderDice();
  renderSnake();
  renderKathmanduBoard();
})();
