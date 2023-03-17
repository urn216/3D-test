package code.math.matrix;

import code.math.vector.Vector3;

/**
* 3D Vector
*/
public class Matrix
{
  public final int rows;
  public final int cols;
  public double[][] values;

  public Matrix(double[][] values) {
    this.values = values;
    this.rows = values.length;
    this.cols = values[0].length;
  }

  public Matrix(Vector3 values) {
    double[][] vals = {{values.x}, {values.y}, {values.z}};
    this.values = vals;
    this.rows = 3;
    this.cols = 1;
  }

  public static Matrix rotateX(double d) {
    double cos = Math.cos(d);
    double sin = Math.sin(d);

    double[][] vals = {
      {1, 0  , 0   },
      {0, cos, -sin},
      {0, sin, cos }
    };
    return new Matrix(vals);
  }

  public static Matrix rotateXLocal(double d, Vector3 dir) {
    Vector3 axis = Matrix.rotateY(Math.PI*0.5).multiply(new Vector3(dir.x, 0, dir.z).unitize());

    double cos = Math.cos(d);
    double sin = Math.sin(d);
    double C = (1-cos);

    double x = axis.x;
    double z = axis.z;

    double[][] vals = {
      {x*x*C + cos, -z*sin, x*z*C      },
      {z*sin      , cos   , -x*sin     },
      {x*z*C      , x*sin , z*z*C + cos}
    };
    return new Matrix(vals);
  }

  public static Matrix rotateY(double d) {
    double cos = Math.cos(d);
    double sin = Math.sin(d);

    double[][] vals = {
      {cos , 0, sin},
      {0   , 1, 0  },
      {-sin, 0, cos}
    };
    return new Matrix(vals);
  }

  public static Matrix rotateLocal(double d, Vector3 axis) {
    double cos = Math.cos(d);
    double sin = Math.sin(d);
    double C = (1-cos);

    double x = axis.x;
    double y = axis.y;
    double z = axis.z;

    double[][] vals = {
      {x*x*C + cos  , x*y*C - z*sin, x*z*C + y*sin},
      {x*y*C + z*sin, y*y*C + cos  , y*z*C - x*sin},
      {x*z*C - y*sin, y*z*C + x*sin, z*z*C + cos  }
    };
    return new Matrix(vals);
  }

  public Vector3 toVector3() {
    if (rows!=3 || cols!=1) return new Vector3();
    return new Vector3(values[0][0], values[1][0], values[2][0]);
  }

  public Matrix multiply(Matrix other) {
    double[][] newVals = new double[this.rows][other.cols];
    for (int i = 0; i < this.rows; i++) {
      for (int j = 0; j < other.cols; j++) {
        double sum = 0;
        for (int k = 0; k < this.cols; k++) {
          sum+=this.values[i][k]*other.values[k][j];
        }
        newVals[i][j] = sum;
      }
    }
    return new Matrix(newVals);
  }

  public Vector3 multiply(Vector3 other) {
    return multiply(new Matrix(other)).toVector3();
  }
}
