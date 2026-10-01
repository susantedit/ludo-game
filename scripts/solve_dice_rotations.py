import math

CUBE_FACES = {
    1: [0, 0, 1],   # Front
    6: [0, 0, -1],  # Back
    2: [0, 1, 0],   # Top
    5: [0, -1, 0],  # Bottom
    3: [1, 0, 0],   # Right
    4: [-1, 0, 0]   # Left
}

def rotate_vector(v, ax, ay, az):
    x, y, z = v
    cx, sx = math.cos(ax), math.sin(ax)
    y1 = y * cx - z * sx
    z1 = y * sx + z * cx
    cy, sy = math.cos(ay), math.sin(ay)
    x2 = x * cy + z1 * sy
    z2 = -x * sy + z1 * cy
    cz, sz = math.cos(az), math.sin(az)
    x3 = x2 * cz - y1 * sz
    y3 = x2 * sz + y1 * cz
    return [x3, y3, z2]

top_target = rotate_vector([0, 1, 0], 0.615, 0.785, 0)

results = {}

for face, norm in CUBE_FACES.items():
    best_dist = 999
    best_angles = None
    
    # Check angles in steps
    # We can try rotating around X, Y, Z
    steps = 16
    for ix in range(-steps, steps + 1):
        ax = ix * (math.pi / 4) + 0.615
        for iy in range(-steps, steps + 1):
            ay = iy * (math.pi / 4) + 0.785
            for iz in range(-steps, steps + 1):
                az = iz * (math.pi / 4)
                
                v_rot = rotate_vector(norm, ax, ay, az)
                d = (v_rot[0]-top_target[0])**2 + (v_rot[1]-top_target[1])**2 + (v_rot[2]-top_target[2])**2
                if d < 1e-3:
                    score = abs(ax) + abs(ay) + abs(az)
                    if d < best_dist - 1e-5 or (abs(d - best_dist) < 1e-5 and score < best_score):
                        best_dist = d
                        best_angles = (round(ax, 3), round(ay, 3), round(az, 3))
                        best_score = score
                        
    results[face] = best_angles
    print(f"Face {face}: angles = {best_angles}")

print("\nFinal Verification:")
for face, angles in results.items():
    all_rot = {f: rotate_vector(CUBE_FACES[f], *angles) for f in CUBE_FACES}
    top_face = max(all_rot.keys(), key=lambda f: all_rot[f][1])
    # check that top_face is facing the camera (z > 0)
    top_z = all_rot[top_face][2]
    print(f"Face {face} -> top face is {top_face}, top_y={round(all_rot[top_face][1], 3)}, top_z={round(top_z, 3)}, angles={angles}")
