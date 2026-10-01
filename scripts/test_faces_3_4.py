import math

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

CUBE_FACES = {
    1: [0, 0, 1],   # Front
    6: [0, 0, -1],  # Back
    2: [0, 1, 0],   # Top
    5: [0, -1, 0],  # Bottom
    3: [1, 0, 0],   # Right
    4: [-1, 0, 0]   # Left
}

# The target top normal is [0.408, 0.817, 0.408].
# In fact, ANY orientation where the rotated normal of face F has y > 0.75 and z > 0.35
# AND face F has the highest Y among all 6 faces will make face F the TOP FACE in the isometric render!
# Let's verify this!

# Let's search over fine angles for face 3 and 4:
for f in [3, 4]:
    norm = CUBE_FACES[f]
    found = []
    for ix in range(-18, 19):
        ax = ix * 0.1
        for iy in range(-18, 19):
            ay = iy * 0.1
            for iz in range(-18, 19):
                az = iz * 0.1
                rot = rotate_vector(norm, ax, ay, az)
                # check if rot is close to [0.408, 0.817, 0.408]
                d = (rot[0]-0.408)**2 + (rot[1]-0.817)**2 + (rot[2]-0.408)**2
                if d < 0.005:
                    found.append((d, (round(ax, 3), round(ay, 3), round(az, 3))))
    found.sort()
    print(f"Face {f} best matches:", found[:3])
