from PIL import Image, ImageDraw, ImageFont

# Maze layout copied from Maze.java
# We're using (row, col) with row 0 at top.
N = 4
north = [[False, False, False, False],
         [False, False, True, True],
         [True, False, False, False],
         [True, False, False, True]]
south = [[False, False, True, True],
         [True, False, False, False],
         [True, False, False, True],
         [False, False, False, False]]
east  = [[True, True, True, False],
         [True, True, False, False],
         [True, True, True, False],
         [False, True, True, False]]
west  = [[False, True, True, True],
         [False, True, True, False],
         [False, True, True, True],
         [False, False, True, True]]

# Path output from running java MazeRunner earlier (labels printed as "c(3-r)")
# Example raw labels: 03 -> 13 -> 23 -> 22 -> 12 -> 02 -> 01 -> 11 -> 21 -> 31 -> 30
# Each label is two digits: first is column, second is (3 - row).
raw_labels = ["03","13","23","22","12","02","01","11","21","31","30"]
# Convert labels to (row, col)
path = []
for lab in raw_labels:
    c = int(lab[0])
    inv = int(lab[1])
    r = 3 - inv
    path.append((r, c))

CELL = 80
MARGIN = 20
W = CELL*N + MARGIN*2
H = CELL*N + MARGIN*2

frames = []
FONT = ImageFont.load_default()

# helper to draw maze base

def draw_base(draw):
    # background
    draw.rectangle([0,0,W,H], fill=(255,255,255))
    # grid lines
    for r in range(N+1):
        y = MARGIN + r*CELL
        draw.line([(MARGIN, y),(MARGIN+N*CELL, y)], fill=(200,200,200), width=1)
    for c in range(N+1):
        x = MARGIN + c*CELL
        draw.line([(x, MARGIN),(x, MARGIN+N*CELL)], fill=(200,200,200), width=1)
    # walls (thick lines) based on direction arrays
    for r in range(N):
        for c in range(N):
            x0 = MARGIN + c*CELL
            y0 = MARGIN + r*CELL
            x1 = x0 + CELL
            y1 = y0 + CELL
            # north wall: draw if north[r][c] is False
            if not north[r][c]:
                draw.line([(x0, y0),(x1, y0)], fill=(0,0,0), width=4)
            if not south[r][c]:
                draw.line([(x0, y1),(x1, y1)], fill=(0,0,0), width=4)
            if not east[r][c]:
                draw.line([(x1, y0),(x1, y1)], fill=(0,0,0), width=4)
            if not west[r][c]:
                draw.line([(x0, y0),(x0, y1)], fill=(0,0,0), width=4)

    # Draw START and GOAL markers as colored badges with centered letters
    sx = MARGIN + 0*CELL + CELL//2
    sy = MARGIN + 0*CELL + CELL//2
    gx = MARGIN + 3*CELL + CELL//2
    gy = MARGIN + 3*CELL + CELL//2
    badge_r = CELL//5
    # start: green circle with 'S'
    draw.ellipse([(sx-badge_r, sy-badge_r),(sx+badge_r, sy+badge_r)], fill=(0,150,0), outline=(0,100,0), width=2)
    bbox = draw.textbbox((0,0), 'S', font=FONT)
    tw = bbox[2] - bbox[0]
    th = bbox[3] - bbox[1]
    draw.text((sx - tw/2, sy - th/2), 'S', font=FONT, fill=(255,255,255))
    # goal: red circle with 'G'
    draw.ellipse([(gx-badge_r, gy-badge_r),(gx+badge_r, gy+badge_r)], fill=(180,0,0), outline=(120,0,0), width=2)
    bbox = draw.textbbox((0,0), 'G', font=FONT)
    tw = bbox[2] - bbox[0]
    th = bbox[3] - bbox[1]
    draw.text((gx - tw/2, gy - th/2), 'G', font=FONT, fill=(255,255,255))

# Create frames: base + agent at each step
for i in range(len(path)):
    im = Image.new('RGB', (W,H), (255,255,255))
    d = ImageDraw.Draw(im)
    draw_base(d)
    # draw visited path so far
    for j in range(i+1):
        r,c = path[j]
        cx = MARGIN + c*CELL + CELL//2
        cy = MARGIN + r*CELL + CELL//2
        radius = CELL//6
        d.ellipse([(cx-radius, cy-radius),(cx+radius, cy+radius)], fill=(100,180,255))
    # draw agent (current)
    r,c = path[i]
    cx = MARGIN + c*CELL + CELL//2
    cy = MARGIN + r*CELL + CELL//2
    rads = CELL//3
    d.ellipse([(cx-rads, cy-rads),(cx+rads, cy+rads)], fill=(255,120,80))
    # step label
    d.text((10, H-18), f"Step: {i}", fill=(50,50,50))
    frames.append(im)

# Save GIF
frames[0].save('maze_solve.gif', save_all=True, append_images=frames[1:], duration=600, loop=0)
print('Saved maze_solve.gif')
