package org.axcommunity.niagara.extensions;

import javax.baja.control.*;
import javax.baja.status.*;
import javax.baja.sys.*;

/**
 * {@code BFilterExt} is a numeric point extension that applies a first-order
 * IIR low-pass filter to its parent point's value.
 * <p>
 * Each time the parent point executes, the raw value is smoothed using the
 * trapezoidal (bilinear-transform) one-pole filter:
 * <pre>
 *   f    = filter / 100                 (filter coefficient, 0&lt;f&le;1)
 *   gain = (1 - f) / (1 + f)            (pole)
 *   a    =      f  / (1 + f)
 *   y[n] = gain * y[n-1] + a * (x[n] + x[n-1])
 * </pre>
 * <p>
 * <b>The {@code filter} scale is inverted from intuition:</b> a <i>low</i>
 * value gives <i>heavy</i> smoothing / slow response (pole near 1), while a
 * <i>high</i> value (up to 100) gives <i>light</i> smoothing. For example:
 * <ul>
 *   <li>{@code filter = 5} (default) &rarr; pole 0.905, heavy smoothing</li>
 *   <li>{@code filter = 50} &rarr; pole 0.33, light smoothing</li>
 *   <li>{@code filter = 100} &rarr; pole 0, output is roughly the mean of the
 *       last two samples (minimal smoothing)</li>
 * </ul>
 * The value is clamped to the range 1&ndash;100; a value of 0 is disallowed
 * because it would freeze the output.
 * <p>
 * Filter state ({@code prevIn}/{@code prevOut}) is persisted, so after a station
 * restart the filter resumes from where it left off instead of warming up from
 * zero. On first use it is seeded from the first sample, so the initial output
 * passes through unfiltered (no startup spike).
 *
 * @author    Dean Mynott - Ronin Control Systems Pty Ltd
 * @creation  16 June 2011
 */
public class BFilterExt extends BPointExtension
{

////////////////////////////////////////////////////////////////
// Property "filter"
////////////////////////////////////////////////////////////////

  /**
   * Filter strength, 1&ndash;100. <b>Lower = heavier smoothing / slower
   * response; higher = lighter smoothing.</b> A value of 0 is not allowed
   * (it would freeze the output).
   */
  public static final Property filter = newProperty(Flags.SUMMARY, new BStatusNumeric(5),
      BFacets.make(BFacets.MIN, BInteger.make(1), BFacets.MAX, BInteger.make(100)));
  public BStatusNumeric getFilter() { return (BStatusNumeric)get(filter); }
  public void setFilter(BStatusNumeric v) { set(filter,v,null); }

////////////////////////////////////////////////////////////////
// Persisted filter state (hidden)
////////////////////////////////////////////////////////////////

  /** Previous raw input sample x[n-1]; persisted so the filter survives restarts. */
  public static final Property prevIn = newProperty(Flags.HIDDEN, 0d, null);
  public double getPrevIn() { return getDouble(prevIn); }
  public void setPrevIn(double v) { setDouble(prevIn, v, null); }

  /** Previous filtered output y[n-1]; persisted so the filter survives restarts. */
  public static final Property prevOut = newProperty(Flags.HIDDEN, 0d, null);
  public double getPrevOut() { return getDouble(prevOut); }
  public void setPrevOut(double v) { setDouble(prevOut, v, null); }

  /** True once the filter state has been seeded from the first sample. */
  public static final Property seeded = newProperty(Flags.HIDDEN, false, null);
  public boolean getSeeded() { return getBoolean(seeded); }
  public void setSeeded(boolean v) { setBoolean(seeded, v, null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////

  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BFilterExt.class);

////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////

  public BFilterExt()
  {
  }

////////////////////////////////////////////////////////////////
//  Parent checking
////////////////////////////////////////////////////////////////

  /**
   * Forces the parent to be a numeric point.
   */
  public boolean isParentLegal(BComponent parent)
  {
    return (parent instanceof BNumericPoint);
  }

////////////////////////////////////////////////////////////////
// Update Methods
////////////////////////////////////////////////////////////////

  public void changed(Property p, Context cx)
  {
    super.changed(p, cx);
    if (!isRunning())
      return;
    if (p.equals(filter))
    {
      BControlPoint parent = getParentPoint();
      if (parent != null) parent.execute();
    }
  }

  /**
   * Called when either me or my parent control point is updated.
   * Applies the one-pole low-pass filter and writes the result back to the
   * parent point's output value.
   */
  public void onExecute(BStatusValue o, Context cx)
  {
    BStatusNumeric out = (BStatusNumeric)o;          // parent output
    double xCur = out.getValue();                    // current raw input

    // Clamp the coefficient to (0,1]; 0 would freeze the output.
    double fv = getFilter().getValue();
    if (fv < 1)   fv = 1;
    if (fv > 100) fv = 100;
    double f = fv / 100.0;

    // First execution after being added: seed state from the current sample so
    // the initial output passes through unfiltered (no startup spike). State is
    // persisted thereafter, so restarts resume rather than re-seed.
    if (!getSeeded())
    {
      setPrevIn(xCur);
      setPrevOut(xCur);
      setSeeded(true);
    }

    double xPrev = getPrevIn();
    double yPrev = getPrevOut();

    double gain = (1 - f) / (f + 1);
    double a    =      f  / (f + 1);

    double yCur = gain * yPrev + a * (xCur + xPrev);  // y[n] = gain*y[n-1] + a*(x[n]+x[n-1])

    o.setValueValue(BDouble.make(yCur));              // write filtered value to parent.out

    // Persist state for the next sample / restart (skip redundant writes).
    if (xPrev != xCur) setPrevIn(xCur);
    if (yPrev != yCur) setPrevOut(yCur);
  }

  // icon for this component
  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/Ronin16.png");
}


/***************************************************

One-Liner IIR Filters (1st order)

Type : IIR 1-pole
References : Posted by chris at ariescode dot com

Notes :
Here is a collection of one liner IIR filters.
Each filter has been transformed into a single C++ expression.

The filter parameter is f or g, and the state variable that needs to be kept around between interations is s.

- Christian

Code :
    101 Leaky Integrator

        a0 = 1
        b1 = 1 - f

        out = s += in - f * s;


    102 Basic Lowpass (all-pole)

        A first order lowpass filter, by finite difference appoximation (differentials --> differences).

        a0 = f
    b1 = 1 - f

        out = s += f * ( in - s );


    103 Lowpass with inverted control

    Same as above, except for different filter parameter is now inverted.
        In this case, g equals the location of the pole.

        a0 = g - 1
    b1 = g

        out = s = in + g * ( s - in );


    104 Lowpass with zero at Nyquist

        A first order lowpass filter, by via the conformal map of the z-plane (0..infinity --> 0..Nyquist).

        a0 = f
        a1 = f
        b1 = 1 - 2 * f

    s = temp + ( out = s + ( temp = f * ( in - s ) ) );


    105 Basic Highpass (DC-blocker)

        Input complement to basic lowpass, yields a finite difference highpass filter.

        a0 = 1 - f
        a1 = f - 1
        b1 = 1 - f

        out = in - ( s += f * ( in - s ) );


    106 Highpass with forced unity gain at Nyquist

        Input complement to filter 104, yields a conformal map highpass filter.

        a0 = 1 - f
        a1 = f - 1
        b1 = 1 - 2 * f

        out = in + temp - ( s += 2 * ( temp = f * ( in - s ) ) );


    107 Basic Allpass

        This corresponds to a first order allpass filter,
        where g is the location of the pole in the range -1..1.

        a0 = -g
        a1 = 1
        b1 = g

    s = in + g * ( out = s - g * in );


 ********************************************************/
