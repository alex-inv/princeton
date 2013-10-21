import java.util.TreeSet;

public class PointSET {
    private TreeSet<Point2D> pointSet;

    public PointSET() {
        pointSet = new TreeSet<Point2D>();
    }

    public boolean isEmpty() {
        return pointSet.size() == 0;
    }

    public int size() {
        return pointSet.size();
    }

    public void insert(Point2D point) {
        pointSet.add(point);
    }

    public boolean contains(Point2D point) {
        return pointSet.contains(point);
    }

    public void draw() {
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(.01);

        for (Point2D point : pointSet) {
            point.draw();
        }
    }

    public Iterable<Point2D> range(RectHV rect) {
        Queue<Point2D> queue = new Queue<Point2D>();

        for (Point2D point2d : pointSet) {
            if (rect.contains(point2d)) {
                queue.enqueue(point2d);
            }
        }

        return queue;
    }

    public Point2D nearest(Point2D point) {
        if (isEmpty()) {
            return null;
        } else {
            Point2D minPoint = null;

            for (Point2D point2d : pointSet) {
                if (minPoint == null
                        || point2d.distanceSquaredTo(point) < minPoint.distanceSquaredTo(point)) {
                    minPoint = point2d;
                }
            }

            return minPoint;
        }
    }
}
