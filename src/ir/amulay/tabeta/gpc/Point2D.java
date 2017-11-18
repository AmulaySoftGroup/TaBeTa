package ir.amulay.tabeta.gpc;

/**
 * Simple class for storing 2D coordinates, mimicing the java.awt.Point2D class.
 * @author dlegland
 *
 */
public class Point2D {

	double x;
	double y;
	
	public Point2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Point2D() {
		this(0, 0);
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	public void SetY(double Y) {
		this.y = Y;
	}
	public void SetX(double X) {
		this.x = X;
	}
	public void rotateZ(double theta) {
		double cos = Math.cos(theta);
		double sin = Math.sin(theta);
		double x1 = x * cos + y * sin;
		double y1 = x * -sin + y * cos;
		x = x1;
		y = y1;
	}
	public void translate(double dx, double dy) {
		x += dx;
		y += dy;
	}
}
