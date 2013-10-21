public class KdTree {
    private static final double XMIN = 0.0;
    private static final double XMAX = 1.0;
    private static final double YMIN = 0.0;
    private static final double YMAX = 1.0;

    private int size;

    private Node root;

    private class Node {
        private Point2D p;
        private RectHV rect;

        private Node left;
        private Node right;

        private Node(Point2D value, RectHV inRect) {
            p = value;
            rect = inRect;

            left = null;
            right = null;
        }
    }

    public KdTree() {
        size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void insert(Point2D point) {
        root = insert(root, point, XMIN, YMIN, XMAX, YMAX, 0);
    }

    public boolean contains(Point2D point) {
        return (contains(root, point, 0) != null);
    }

    private int cmp(Point2D a, Point2D b, int level) {
        if (level % 2 == 0) {
            // Compare x-coordinates
            int cmpResult = new Double(a.x()).compareTo(new Double(b.x()));

            if (cmpResult == 0) {
                return new Double(a.y()).compareTo(new Double(b.y()));
            } else {
                return cmpResult;
            }
        } else {
         // Compare y-coordinates
            int cmpResult = new Double(a.y()).compareTo(new Double(b.y()));

            if (cmpResult == 0) {
                return new Double(a.x()).compareTo(new Double(b.x()));
            } else {
                return cmpResult;
            }
        }
    }

    private Node insert(Node x, Point2D value, double xmin, double ymin, double xmax, double ymax, int level) {
        if (x == null) {
            size++;
            return new Node(value, new RectHV(xmin, ymin, xmax, ymax));
        };

        int cmp = cmp(value, x.p, level);

        if (cmp < 0) {
            if (level % 2 == 0) {
                x.left = insert(x.left, value, xmin, ymin, x.p.x(), ymax, level + 1);
            } else {
                x.left = insert(x.left, value, xmin, ymin, xmax, x.p.y(), level + 1);
            }
        } else if (cmp > 0) {
            if (level % 2 == 0) {
                x.right = insert(x.right, value, x.p.x(), ymin, xmax, ymax, level + 1);
            } else {
                x.right = insert(x.right, value, xmin, x.p.y(), xmax, ymax, level + 1);
            }
        }

        return x;
    }

    private Point2D contains(Node x, Point2D point, int level) {
        while (x != null) {

            int cmp = cmp(point, x.p, level);

            if (cmp < 0) {
                return contains(x.left, point, level + 1);
            } else if (cmp > 0) {
                return contains(x.right, point, level + 1);
            } else {
                return x.p;
            }
        }

        return null;
    }

    public void draw() {
        StdDraw.clear();

        drawLine(root, 0);
    }

    private void drawLine(Node x, int level) {
        if (x != null) {
            drawLine(x.left, level + 1);

            StdDraw.setPenRadius();
            if (level % 2 == 0) {
                StdDraw.setPenColor(StdDraw.RED);
                StdDraw.line(x.p.x(), x.rect.ymin(), x.p.x(), x.rect.ymax());
            } else {
                StdDraw.setPenColor(StdDraw.BLUE);
                StdDraw.line(x.rect.xmin(), x.p.y(), x.rect.xmax(), x.p.y());
            }

            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(.01);
            x.p.draw();

            drawLine(x.right, level + 1);
        }
    }

    public Iterable<Point2D> range(RectHV rect) {
        Queue<Point2D> queue = new Queue<Point2D>();

        rangeAdd(root, rect, queue);

        return queue;
    }

    private void rangeAdd(Node x, RectHV rect, Queue<Point2D> queue) {
        if (x != null && rect.intersects(x.rect)) {
            if (rect.contains(x.p)) {
                queue.enqueue(x.p);
            }

            rangeAdd(x.left, rect, queue);
            rangeAdd(x.right, rect, queue);
        }
    }

    public Point2D nearest(Point2D point) {
        if (isEmpty()) {
            return null;
        } else {
            Point2D result = null;

            result = nearest(root, point, result);

            return result;
        }
    }

    private Point2D nearest(Node x, Point2D point, Point2D min) {
        if (x != null) {
            if (min == null) {
                min = x.p;
            }

            // If the current min point is closer to query than the current point
            if (min.distanceSquaredTo(point)
                    >= x.rect.distanceSquaredTo(point)) {
                if (x.p.distanceSquaredTo(point) < min.distanceSquaredTo(point)) {
                    min = x.p;
                }

                // Check in which order should we iterate
                if (x.right != null && x.right.rect.contains(point)) {
                    min = nearest(x.right, point, min);
                    min = nearest(x.left, point, min);
                } else {
                    min = nearest(x.left, point, min);
                    min = nearest(x.right, point, min);
                }
            }
        }

        return min;
    }
}
