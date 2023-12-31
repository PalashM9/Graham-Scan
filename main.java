import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class Main {

    public static void main(String[] args) {
        GrahamScan gs = new GrahamScan();

        List<GrahamScan.Point> points = gs.generatePoints(5, 0, 10, 3, 20);  //To generate set of random points
        System.out.println(points);
        System.out.println(gs.scan(points));
    }

    static public class GrahamScan {

        Point anchor;

		// generate specified numbr of points. Set minimum and maximum x and y values.
        public List<Point> generatePoints(int size, int x_min, int x_max, int y_min, int y_max) {
            List<Point> points = new ArrayList<>();

            Random r = new Random();

            for (int i = 0; i < size; i++) {
                Point p = new Point();
                p.x = x_min + r.nextInt(x_max);
                p.y = y_min + r.nextInt(y_max);
                points.add(p);
            }

            return points;

        }

		// calculate polar angle 
        public double getPolarAngle(Point p1, Point p2) {

            int xd = p1.x - p2.x;
            int yd = p1.y - p2.y;

            return Math.atan2(yd, xd);

        }

		// calculate distance
        public double distance(Point p1, Point p2) {

            if (p2 == null) {
                p2 = anchor;
            }

            int xd = p1.x - p2.x;
            int yd = p1.y - p2.y;

            return ((xd * xd) + (yd * yd));

        }

		// calculate the rotation direction between 3 points
        public int clock(Point p1, Point p2, Point p3) {
            // -ve = clockwise
            // +ve = anticlockwise
			// 0 = co linear
            return ((p2.x - p1.x) * (p3.y - p1.y)) -
                    ((p2.y - p1.y) * (p3.x - p1.x));
        }

		// merge sort wrt anchor i.e. p0 
        List<Point> mergeSort(Point p0, List<Point> unsortedArray) {

            if (unsortedArray.size() == 1) {
                return List.of(unsortedArray.get(0));
            }

            int idxL = 0;
            int idxR = unsortedArray.size();
            int mid = (int) Math.floor((idxL + idxR) / 2);

            List<Point> leftSide = mergeSort(p0, unsortedArray.subList(idxL, mid));
            List<Point> rightSide;
            if (idxR == mid + 1) {
                rightSide = mergeSort(p0, List.of(unsortedArray.get(idxR - 1)));
            } else {
                rightSide = mergeSort(p0, unsortedArray.subList(mid, idxR));
            }

            return merge(p0, leftSide, rightSide); // To call the merge function

        }

		// merge two list based on angle and distance from p0 
        List<Point> merge(Point p0, List<Point> leftList, List<Point> rightList) {
            List<Point> mergedList = new ArrayList();

            int lenL = leftList.size();
            int lenR = rightList.size();

            int idxL = 0;
            int idxR = 0;

            while (true) {
                Point pL = null;
                Point pR = null;

                if (idxL < lenL)
                    pL = leftList.get(idxL);

                if (idxR < lenR)
                    pR = rightList.get(idxR);

                if (pL == null && pR == null) {
                    break;
                }
                if (pL == null) {
                    if (pR != p0) {
                        mergedList.add(pR);
                    }
                    idxR++;
                    continue;
                }
                if (pR == null) {
                    if (pL != p0) {
                        mergedList.add(pL);
                    }
                    idxL++;
                    continue;
                }

                double angle1 = getPolarAngle(p0, pL);
                double angle2 = getPolarAngle(p0, pR);

                if (angle1 == angle2) {

                    double disR = distance(p0, pR);
                    double disL = distance(p0, pL);

                    if(disR < disL){
                        mergedList.add(pR);
                        mergedList.add(pL);
                    } else {
                        mergedList.add(pL);
                        mergedList.add(pR);
                    }

                    idxL++;
                    idxR++;

                } else if (angle1 <= angle2) {
                    if (pL != p0) {
                        mergedList.add(pL);
                    }

                    idxL++;

                } else {
                    if (pR != p0) {
                        mergedList.add(pR);
                    }
                    idxR++;
                }
            }

            return mergedList;
        }

		// find the minimum point 
        Point findAnchor(List<Point> points) {

            Point min = null;
            int minY = Integer.MAX_VALUE;

            for (Point p : points) {
                if (p.y < minY) {
                    min = p;
                    minY = p.y;
                }
                if (p.y == minY) {
                    if (p.x < min.x) {
                        min = p;
                        minY = p.y;
                    }
                }

            }
            return min;
        }

		// apply graham scan algorithm 
        Stack<Point> scan(List<Point> points) {

            this.anchor = findAnchor(points);
            List<Point> sortedPoints = mergeSort(this.anchor, points);
            System.out.println("anchor: " + anchor);
            System.out.println("Sorted: " + sortedPoints);

            Stack<Point> hull = new Stack<>();
            hull.push(sortedPoints.get(0));
            hull.push(this.anchor);

            for (int i = 1; i < sortedPoints.size(); i++) {
                Point s = sortedPoints.get(i);
                Point lastPoint = hull.elementAt(hull.size() - 1);
                Point secondLastPoint = hull.elementAt(hull.size() - 2);

                while (clock(secondLastPoint, lastPoint, s) <= 0) {
                    hull.pop();
                    if (hull.size() < 2) {
                        break;
                    }
                }

                hull.push(s);

            }
            hull.push(this.anchor);
            return hull;


        }

        static class Point {
            int x, y;

            public Point() {

            }


            public Point(int x, int y) {
                this.x = x;
                this.y = y;
            }

            @Override
            public String toString() {
                return "(" + x + ", " + y + ")";
            }
        }
    }

}