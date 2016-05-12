package org.axcommunity.niagara.conversion;

import javax.baja.status.BIStatus;
import javax.baja.status.BStatus;
import javax.baja.status.BStatusNumeric;
import javax.baja.status.BStatusString;
import javax.baja.sys.*;
import java.text.NumberFormat;


/**
 * Converts a StatusNumeric input to a StatusString ouput. Based to the one from
 * Tridium, but with no leading/trailing 0's
 * 
 * @author Mike Arnott, Kors Engineering
 */
public class BStatusNumericToStatusStringNoPad extends BComponent implements BIStatus {
	

		/**
		 * Slot for the <code>out</code> property.
		 */
		public static final Property out = newProperty(Flags.TRANSIENT|Flags.READONLY|Flags.SUMMARY, new BStatusString(),null);

		/**
		 * Get the <code>out</code> property.
		 */
		public BStatusString getOut() { return (BStatusString)get(out); }

		/**
		 * Set the <code>out</code> property.
		 */
		public void setOut(BStatusString v) { set(out,v,null); }

		/**
		 * Slot for the <code>in</code> property.
		 */
		public static final Property in = newProperty(Flags.TRANSIENT|Flags.SUMMARY, new BStatusNumeric(),null);

		/**
		 * Get the <code>in</code> property.
		 */
		public BStatusNumeric getIn() { return (BStatusNumeric)get(in); }

		/**
		 * Set the <code>in</code> property.
		 */
		public void setIn(BStatusNumeric v) { set(in,v,null); }

		/**
		 * Slot for the <code>integerDigits</code> property.
		 */
		public static final Property integerDigits = newProperty(0, 6,null);

		/**
		 * Get the <code>integerDigits</code> property.
		 */
		public int getIntegerDigits() { return getInt(integerDigits); }

		/**
		 * Set the <code>integerDigits</code> property.
		 */
		public void setIntegerDigits(int v) { setInt(integerDigits,v,null); }


		/**
		 * Slot for the <code>decimalDigits</code> property.
		 */
		public static final Property decimalDigits = newProperty(0, 6,null);

		/**
		 * Get the <code>decimalDigits</code> property.
		 * 
		 * @see com.tridium.kitControl.conversion.BStatusNumericToStatusString#decimalDigits
		 */
		public int getDecimalDigits() { return getInt(decimalDigits); }

		/**
		 * Set the <code>decimalDigits</code> property.
		 * 
		 * @see com.tridium.kitControl.conversion.BStatusNumericToStatusString#decimalDigits
		 */
		public void setDecimalDigits(int v) { setInt(decimalDigits,v,null); }


		/**
		 * Init if started after steady state has been reached.
		 */
		public void started()
		{
			calculate();
		}

		/**
		 * setoutput on in change.
		 */
		public void changed(Property p, Context cx)
		{
			if (!isRunning()) return;

			if (p == in || p == decimalDigits || p == integerDigits)
			{
				calculate();
			}
		}

		void calculate()
		{
			double inValue = getIn().getValue();
			workingValue.setStatus(getIn().getStatus());
			int precision = getDecimalDigits();
			int integer   = getIntegerDigits();
			NumberFormat format = NumberFormat.getNumberInstance();
			format.setMaximumFractionDigits(precision);
			format.setMinimumFractionDigits(0);
			format.setMaximumIntegerDigits(integer);
			format.setMinimumIntegerDigits(1);
			format.setGroupingUsed(false);
			String value = format.format(inValue);
			try
			{
				workingValue.setValue(value);
			}
			catch(Exception e)
			{
				workingValue.setStatusNull(true);
				workingValue.setStatusFault(true);
			}
			setOut(workingValue);
		}

		public String toString(Context cx)
		{
			return getOut().toString(cx);
		}

		public BStatus getStatus() { return getOut().getStatus(); }

		public BIcon getIcon() { return icon; }
		private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");

		public static final Type TYPE = Sys.loadType(BStatusNumericToStatusStringNoPad.class);
		public Type getType() { return TYPE; }

		BStatusString workingValue = new BStatusString();

	}
