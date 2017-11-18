package ir.amulay.tabeta.gpc;



/**
 * <code>Poly</code> is an interface to a complex polygon.  <code>Poly</code> polygons
 * can consist of  multiple "inner" polygons that can be disjoint and can be considered holes.
 * <p>
 * Currently, this interface supports two concepts:
 * <ul>
 *    <li>a set of inner polygons</li>
 *    <li>a set of points of a polygon</li>
 * </ul>
 * <p>
 * <b>Refactoring.</b> This would be a good place for some refactoring to create
 * a ComplexPoly and an InnerPoly or something so that these two concepts are broken
 * out.  One might also consider changing from an interface to an abstract class,
 * so the methods <code>isContributing()</code> and <code>setContributing()</code>
 * could have an access of package only.  Or, the <code>Clip</code> algorithm could 
 * not store this information in the <code>Poly</code>.
 * <p>
 * <b>Assumptions.</b> The methods that access the polygon as though it were a set of points assume
 * it is accessing the first polygon in the list of inner polygons.  It is also assumed that
 * inner polygons do not have more inner polygons.
 *
 * @author  Dan Bridenbecker, Solution Engineering, Inc.
 */
public interface Poly
{
   // ----------------------
   // --- Public Methods ---
   // ----------------------
   /**
    * Remove all of the points.  Creates an empty polygon.
    */
   public void clear();
   /**
    * Remove One of the points.  Creates an empty polygon.
    */
   public Point2D Remove();
   
   /**
    * Add a point to the first inner polygon.
    */
   public void add( double x, double y );
   
   /**
    * Add a point to the first inner polygon.
    */
   public void add(Point2D p );
   
   /**
    * Add an inner polygon to this polygon - assumes that adding polygon does not
    * have any inner polygons.
    */
   public void add( Poly p );
   
   /**
    * Return true if the polygon is empty
    */
   public boolean isEmpty();
   
   /**
    * Returns the bounding rectangle of this polygon.
    */
   public Rectangle2D getBounds();
   
   /**
    * Returns the polygon at this index.
    */
   public Poly getInnerPoly( int polyIndex );
   
   /**
    * Returns the number of inner polygons - inner polygons are assumed to return one here.
    */
   public int getNumInnerPoly();   
   
   /**
    * Return the number points of the first inner polygon
    */
   public int getNumPoints();
   
   /**
    * Return the number points of the first inner polygon
    */
   public void PrintVertices();
   
   /**
    * Return the X value of the point at the index in the first inner polygon
    */
   public double getX( int index );
   
   /**
    * Return the Y value of the point at the index in the first inner polygon
    */
   public double getY( int index );
   
   /**
    * Return true if this polygon is a hole.  Holes are assumed to be inner polygons of
    * a more complex polygon.
    *
    * @throws IllegalStateException if called on a complex polygon.
    */
   public boolean isHole();
   
   /**
    * Set whether or not this polygon is a hole.  Cannot be called on a complex polygon.
    *
    * @throws IllegalStateException if called on a complex polygon.
    */
   public void setIsHole( boolean isHole );
   
   /**
    * Return true if the given inner polygon is contributing to the set operation.
    * This method should NOT be used outside the Clip algorithm.
    */
   public boolean isContributing( int polyIndex );
   
   /**
    * Set whether or not this inner polygon is constributing to the set operation.
    * This method should NOT be used outside the Clip algorithm.
    */
   public void setContributing( int polyIndex, boolean contributes );
   
   /**
    * Return a Poly that is the intersection of this polygon with the given polygon.
    * The returned polygon could be complex.
    */
   public Poly intersection( Poly p );
      
   /**
    * Return a Poly that is the union of this polygon with the given polygon.
    * The returned polygon could be complex.
    */
   public Poly union( Poly p );
   
   /**
    * Return a Poly that is the exclusive-or of this polygon with the given polygon.
    * The returned polygon could be complex.
    */
   public Poly xor( Poly p );
   
   /**
    * Return a Poly that is the difference of this polygon with the given polygon.
    * The returned polygon could be complex.
    */
   public Poly difference( Poly p );
   
   /**
    * Return the area of the polygon in square units.
    */
   public double getArea();
}

