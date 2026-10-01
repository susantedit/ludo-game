import math

def mat_mul_vec(m, v):
    return [
        m[0][0]*v[0] + m[0][1]*v[1] + m[0][2]*v[2],
        m[1][0]*v[0] + m[1][1]*v[1] + m[1][2]*v[2],
        m[2][0]*v[0] + m[2][1]*v[1] + m[2][2]*v[2],
    ]

def rot_x(a):
    c, s = math.cos(a), math.sin(a)
    return [
        [1, 0, 0],
        [0, c, -s],
        [0, s, c]
    ]

def rot_y(a):
    c, s = math.cos(a), math.sin(a)
    return [
        [c, 0, s],
        [0, 1, 0],
        [-s, 0, c]
    ]

def rot_z(a):
    c, s = math.cos(a), math.sin(a)
    return [
        [c, -s, 0],
        [s, c, 0],
        [0, 0, 1]
    ]

def mat_mul(a, b):
    res = [[0, 0, 0] for _ in range(3)]
    for r in range(3):
        for c in range(3):
            for k in range(3):
                res[r][c] += a[r][k] * b[k][c]
    return res

CUBE_FACES = {
    1: [0, 0, 1],   # Front (+Z)
    6: [0, 0, -1],  # Back (-Z)
    2: [0, 1, 0],   # Top (+Y)
    5: [0, -1, 0],  # Bottom (-Y)
    3: [1, 0, 0],   # Right (+X)
    4: [-1, 0, 0]   # Left (-X)
}

# Rotations that map face normal to +Y:
face_to_top = {
    2: rot_x(0),                      # Identity
    5: rot_x(math.pi),                # 180 deg on X
    1: rot_x(-math.pi / 2),           # -90 deg on X
    6: rot_x(math.pi / 2),            # +90 deg on X
    3: rot_z(math.pi / 2),            # +90 deg on Z
    4: rot_z(-math.pi / 2),           # -90 deg on Z
}

# Isometric camera rotation:
# First rot_y(0.785), then rot_x(0.615)
R_cam = mat_mul(rot_x(0.615), rot_y(0.785))

print("VERIFYING EACH FACE ON TOP:")
for face_val in [1, 2, 3, 4, 5, 6]:
    R_face = face_to_top[face_val]
    # Total matrix: R_cam * R_face
    M = mat_mul(R_cam, R_face)
    
    # Calculate rotated normal for all faces:
    all_norms = {}
    for f, n in CUBE_FACES.items():
        all_norms[f] = mat_mul_vec(M, n)
        
    # The top face on screen is the one with the maximum Y in camera space!
    top_face = max(all_norms.keys(), key=lambda f: all_norms[f][1])
    top_n = all_norms[top_face]
    print(f"Goal: {face_val} -> Top Face is {top_face}! Normal={round(top_n[0], 3), round(top_n[1], 3), round(top_n[2], 3)}")
    assert top_face == face_val, f"Failed for face {face_val}"

print("\nALL 6 FACES 100% VERIFIED TO BE ON TOP!")
