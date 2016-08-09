package org.axcommunity.niagara.string;

import javax.baja.status.BStatus;
import javax.baja.status.BStatusBoolean;
import javax.baja.status.BStatusString;
import javax.baja.sys.BAbsTime;
import javax.baja.sys.BComponent;
import javax.baja.sys.BDynamicEnum;
import javax.baja.sys.BEnumRange;
import javax.baja.sys.BFacets;
import javax.baja.sys.BIcon;
import javax.baja.sys.BInteger;
import javax.baja.sys.BObject;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;

public class BSuperStringTest extends BComponent
{
  public static final Property numberOfTests = newProperty(0, 0, BFacets.make(BFacets.MIN, BInteger.make(0), BFacets.MAX, BInteger.make(60)));
  public int getNumberOfTests() { return getInt(numberOfTests); }
  public void setNumberOfTests(int v) {setInt(numberOfTests,v);}
  
  static final String[] stringCompareOptions = {"Input$20Equals$20Test$20String", "Input$20Equals$20Test$20String$20Ingnore$20Case", "Input$20Starts$20With$20Test$20String", "Input$20Ends$20With$20Test$20String", "Input$20Contains$20Test$20String"};
  
  public static final Property testSelect = newProperty(0, BDynamicEnum.make(1, BEnumRange.make(stringCompareOptions)));
  public BDynamicEnum getTestSelect() { return (BDynamicEnum)get(testSelect); }
  public void setTestSelect(BDynamicEnum v) { set(testSelect,v,null); }
  
  public static final Property in = newProperty(Flags.SUMMARY, new BStatusString("", BStatus.nullStatus),BFacets.make(BFacets.MULTI_LINE, true));
  public BStatusString getIn() { return (BStatusString)get(in); }
  public void setIn(BStatusString v) { set(in, v); }
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BSuperStringTest.class);

  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/EB.png");
  
  public void started()
  {
    if(!Sys.atSteadyState() || !isRunning()) return;
    myCode();
  }
  
  public void atSteadyState() throws Exception
  {
    if(!Sys.atSteadyState() || !isRunning()) return;
    myCode();
  }

  public void changed(Property p, Context cx)
  {
    if(!Sys.atSteadyState() || !isRunning()) return;

    if(p.equals(numberOfTests)) recreateInputs(getNumberOfTests());
    else
    {
      try
      {
        if(p.getName().startsWith("in")) myCode();
      }
      catch (Exception e) {}
    }
  }
  
  private void myCode()
  {
    if(!Sys.atSteadyState() || !isRunning()) return;

    if(!getIn().getStatus().isNull() & getIn().getValue().length() > 0 & getNumberOfTests() > 0);
    {
      try
      {
        for(int i=1; i<getNumberOfTests()+1; i++)
        {
          if(!((BStatusString) ((BObject)get("in"+i))).getStatus().isNull() & ((BStatusString) ((BObject)get("in"+i))).getValue().length() > 0)
          {
            if(!getIn().getStatus().isValid() | getIn().getValue().length() <= 0) ((BStatusBoolean) ((BObject)get("out"+i))).setValue(false);
            else
            {
              if(((BStatusString) ((BObject)get("in"+i))).getStatus().isNull() || ((BStatusString) ((BObject)get("in"+i))).getValue().length() == 0) ((BStatusBoolean) ((BObject)get("out"+i))).setStatus(BStatus.NULL);
              else
              {
                ((BStatusBoolean) ((BObject)get("out"+i))).setStatus(0);
                switch(getTestSelect().getOrdinal())
                {
                  case 0:
                    ((BStatusBoolean) ((BObject)get("out"+i))).setValue(getIn().getValue().equals(((BStatusString) ((BObject)get("in"+i))).getValue()));
                    break;
                  case 1:
                    ((BStatusBoolean) ((BObject)get("out"+i))).setValue(getIn().getValue().equalsIgnoreCase(((BStatusString) ((BObject)get("in"+i))).getValue()));
                    break;
                  case 2:
                    ((BStatusBoolean) ((BObject)get("out"+i))).setValue(getIn().getValue().startsWith(((BStatusString) ((BObject)get("in"+i))).getValue()));
                    break;
                  case 3:
                    ((BStatusBoolean) ((BObject)get("out"+i))).setValue(getIn().getValue().endsWith(((BStatusString) ((BObject)get("in"+i))).getValue()));
                    break;
                  case 4:
                    ((BStatusBoolean) ((BObject)get("out"+i))).setValue(getIn().getValue().indexOf(((BStatusString) ((BObject)get("in"+i))).getValue()) >= 0);
                    break;
                }
              }
            }
          }
        }
      }
      catch (Exception e)
      {
        System.out.println(BAbsTime.now().toLocalTime().toString() + " " + e.getClass().getName());
        System.out.println(this.getSlotPath().toString());
        System.out.println(e.getMessage());
        e.printStackTrace();
      }
    }
  }
  
  
  private void recreateInputs(int SlotCount)
  {
    try
    {
      for(int i=1;i<(SlotCount+1);i++)
      {
        if(((BObject)get("in"+i))       ==null)  {this.add(("in"+i),        new BStatusString("", BStatus.nullStatus), Flags.SUMMARY);}
        if(((BObject)get("out"+i))==null)  {this.add(("out"+i), new BStatusBoolean(false, BStatus.nullStatus), Flags.SUMMARY);}
      }
      
      for(int i=SlotCount+1;
          (BObject)get("in"+i)!=null | 
          (BObject)get("out"+i)!=null; 
          i++)
      {
        if(((BObject)get("in"+i))!=null) {this.remove("in"+i);}             
        if(((BObject)get("out"+i))!=null) {this.remove("out"+i);}             
      }
    }
    catch (Exception e)
    {
      System.out.println(BAbsTime.now().toLocalTime().toString() + " " + e.getClass().getName());
      System.out.println(this.getSlotPath().toString());
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }
}
