package ir.amulay.tabeta.gpc;

import java.util.ArrayList;
import java.util.List;

public class QuickHull
{
    public ArrayList<Point2D> quickHull(List<Point2D> points)
    {
        ArrayList<Point2D> convexHull = new ArrayList<Point2D>();

 
        int minPoint = 0, maxPoint = 0;
        double minX = points.get(0).getX();
        double maxX = points.get(0).getX();
        for (int i = 0; i < points.size(); i++)
        { 
            if (points.get(i).x < minX)
            { 
                minX = points.get(i).getX();
                minPoint = i;
            }
            if (points.get(i).x > maxX)
            {
                maxX = points.get(i).getX();
                maxPoint = i;
            }
        }
        Point2D A = points.get(minPoint);
        Point2D B = points.get(maxPoint);
        convexHull.add(A);
        convexHull.add(B);
        points.remove(A);
        points.remove(B);
 
        ArrayList<Point2D> leftSet = new ArrayList<Point2D>();
        ArrayList<Point2D> rightSet = new ArrayList<Point2D>();
 
        for (int i = 0; i < points.size(); i++)
        {
        	Point2D p = points.get(i);
            if (pointLocation(A, B, p) == -1)
                leftSet.add(p);
            else if (pointLocation(A, B, p) == 1)
                rightSet.add(p);
        }
        hullSet(A, B, rightSet, convexHull);
        hullSet(B, A, leftSet, convexHull);
 
        return convexHull;
    }
 
    public double distance(Point2D A, Point2D B, Point2D C)
    {
        double ABx = B.getX() - A.getX();
        double ABy = B.getY() - A.getY();
        double num = ABx * (A.getY() - C.getY()) - ABy * (A.getX() - C.getX());
        if (num < 0)
            num = -num;
        return num;
    }
 
    public void hullSet(Point2D A, Point2D B, ArrayList<Point2D> set,
            ArrayList<Point2D> hull)
    {
        int insertPosition = hull.indexOf(B);
        if (set.size() == 0)
            return;
        if (set.size() == 1)
        {
            Point2D p = set.get(0);
            set.remove(p);
            hull.add(insertPosition, p);
            return;
        }
        double dist = Float.MIN_VALUE;
        int furthestPoint = -1;
        for (int i = 0; i < set.size(); i++)
        {
            Point2D p = set.get(i);
            double distance = distance(A, B, p);
            if (distance > dist)
            {
                dist = distance;
                furthestPoint = i;
            }
        }
        Point2D P = set.get(furthestPoint);
        set.remove(furthestPoint);
        hull.add(insertPosition, P);
 
        // Determine who's to the left of AP
        ArrayList<Point2D> leftSetAP = new ArrayList<Point2D>();
        for (int i = 0; i < set.size(); i++)
        {
            Point2D M = set.get(i);
            if (pointLocation(A, P, M) == 1)
            {
                leftSetAP.add(M);
            }
        }
 
        // Determine who's to the left of PB
        ArrayList<Point2D> leftSetPB = new ArrayList<Point2D>();
        for (int i = 0; i < set.size(); i++)
        {
            Point2D M = set.get(i);
            if (pointLocation(P, B, M) == 1)
            {
                leftSetPB.add(M);
            }
        }
        hullSet(A, P, leftSetAP, hull);
        hullSet(P, B, leftSetPB, hull);
 
    }
 
    public int pointLocation(Point2D A, Point2D B, Point2D P)
    {
        double cp1 = (B.getX() - A.getX()) * (P.getY() - A.getY()) - (B.getY() - A.getY()) * (P.getX() - A.getX());
        if (cp1 > 0)
            return 1;
        else if (cp1 == 0)
            return 0;
        else
            return -1;
    }
 
}