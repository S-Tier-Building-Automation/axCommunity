package org.axcommunity.niagara.string;

import javax.baja.log.Log;
import javax.baja.status.BStatus;
import javax.baja.status.BStatusString;
import javax.baja.sys.Action;
import javax.baja.sys.BComponent;
import javax.baja.sys.BFacets;
import javax.baja.sys.BIcon;
import javax.baja.sys.BInteger;
import javax.baja.sys.BObject;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;

public class BStringBufferAndSelect extends BComponent
{
  private static String inputPrefix = "in";
  private static String bufferPrefix = "buffer";
  private static String bufferSlotDelimiter = "$2e";
  private static String outputPrefix = "out";
  private static int maxInputs = 60;
  private static int maxBuffers = 60;


  public static final Property numberOfInputs = newProperty(0, 0, BFacets.make(BFacets.MIN, BInteger.make(0), BFacets.MAX, BInteger.make(maxInputs)));
  public int getNumberOfInputs() { return getInt(numberOfInputs); }
  public void setNumberOfInputs(int v) {setInt(numberOfInputs,v);}
  
  public static final Property numberOfBuffers = newProperty(0, 0, BFacets.make(BFacets.MIN, BInteger.make(0), BFacets.MAX, BInteger.make(maxBuffers)));
  public int getNumberOfBuffers() { return getInt(numberOfBuffers); }
  public void setNumberOfBuffers(int v) {setInt(numberOfBuffers,v);}
  
  public static final Property executeOnInputChange = newProperty(0, false);
  public boolean getExecuteOnInputChange() { return getBoolean(executeOnInputChange);}
  public void setExecuteOnInputChange(boolean v) {setBoolean(executeOnInputChange,v);}

  public static final Property executeOnSetBufferChange = newProperty(0, false);
  public boolean getExecuteOnSetBufferChange() { return getBoolean(executeOnSetBufferChange);}
  public void setExecuteOnSetBufferChange(boolean v) {setBoolean(executeOnSetBufferChange,v);}

  public static final Property executeOnSelectBufferChange = newProperty(0, true);
  public boolean getExecuteOnSelectBufferChange() { return getBoolean(executeOnSelectBufferChange);}
  public void setExecuteOnSelectBufferChange(boolean v) {setBoolean(executeOnSelectBufferChange,v);}

  public static final Property setBufferNumber = newProperty(Flags.SUMMARY, 0, BFacets.make(BFacets.MIN, BInteger.make(0), BFacets.MAX, BInteger.make(60)));
  public int getSetBufferNumber() { return getInt(setBufferNumber); }
  public void setSetBufferNumber(int v) {setInt(setBufferNumber,v);}
  
  public static final Property selectBufferNumber = newProperty(Flags.SUMMARY, 0, BFacets.make(BFacets.MIN, BInteger.make(0), BFacets.MAX, BInteger.make(60)));
  public int getSelectBufferNumber() { return getInt(selectBufferNumber); }
  public void setSelectBufferNumber(int v) {setInt(selectBufferNumber,v);}
  
  public static final Action setBuffer = newAction(Flags.SUMMARY,null);
  public void setBuffer() { invoke(setBuffer,null,null);}
  
  public static final Action selectBuffer = newAction(Flags.SUMMARY,null);
  public void selectBuffer() { invoke(selectBuffer,null,null);}
  
  public static final Action resetBuffers = newAction(Flags.SUMMARY,null);
  public void resetBuffers() { invoke(resetBuffers,null,null);}
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BStringBufferAndSelect.class);

  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/EB.png");
  
  
  public void changed(Property p, Context cx)
  {
    if(!Sys.atSteadyState() || !isRunning()) return;

    if(p.equals(numberOfInputs)) recreateInputsAndOutputs(getNumberOfInputs());
    if(p.equals(numberOfBuffers)) recreateBuffers(getNumberOfInputs(), getNumberOfBuffers());
    
    if(p.equals(selectBufferNumber) && getExecuteOnSelectBufferChange()) doSelectBuffer();
    
    if(p.equals(setBufferNumber) && getExecuteOnSetBufferChange()) doSetBuffer();
    else if(getExecuteOnInputChange())
    {
      try
      {
        if(p.getName().startsWith(inputPrefix)) doSetBuffer();
      }
      catch (Exception e) {}
    }
  }
  
  public void doSetBuffer()
  {
    if(!Sys.atSteadyState() || !isRunning() || getSetBufferNumber() == 0) return;

    try
    {
      for(int i=1; i<getNumberOfInputs()+1; i++)
      {
        ((BStatusString) ((BObject)get(bufferPrefix+getSetBufferNumber()+bufferSlotDelimiter+i))).setValue(((BStatusString) ((BObject)get(inputPrefix+i))).getValue());
        ((BStatusString) ((BObject)get(bufferPrefix+getSetBufferNumber()+bufferSlotDelimiter+i))).setStatus(((BStatusString) ((BObject)get(inputPrefix+i))).getStatus());
      }
    }
    catch (Exception e)
    {
      logger.error(getSlotPath().toString() + e.getMessage());
      e.printStackTrace();
    }
    
    if(getSetBufferNumber() == getSelectBufferNumber()) doSelectBuffer();
  }
  
  public void doSelectBuffer()
  {
    if(getSelectBufferNumber() == 0)
    {
      for(int i=1; i<getNumberOfInputs()+1; i++)
      {
        ((BStatusString) ((BObject)get(outputPrefix+i))).setValue("");
        ((BStatusString) ((BObject)get(outputPrefix+i))).setStatus(BStatus.NULL);
      }
    }
    else
    {
      try
      {
        for(int i=1; i<getNumberOfInputs()+1; i++)
        {
          ((BStatusString) ((BObject)get(outputPrefix+i))).setValue(((BStatusString) ((BObject)get(bufferPrefix+getSelectBufferNumber()+bufferSlotDelimiter+i))).getValue());
          ((BStatusString) ((BObject)get(outputPrefix+i))).setStatus(((BStatusString) ((BObject)get(bufferPrefix+getSelectBufferNumber()+bufferSlotDelimiter+i))).getStatus());
        }
      }
      catch (Exception e)
      {
        logger.error(getSlotPath().toString() + e.getMessage());
        e.printStackTrace();
      }
    }
  }
  
  public void doResetBuffers()
  {
    for(int i=1; i<getNumberOfInputs()+1; i++)
    {
      for(int j=1; j<getNumberOfBuffers()+1; j++)
      {
        ((BStatusString) ((BObject)get(bufferPrefix+j+bufferSlotDelimiter+i))).setValue("");
        ((BStatusString) ((BObject)get(bufferPrefix+j+bufferSlotDelimiter+i))).setStatus(BStatus.NULL);
      }
    }
  }
  
  private void recreateInputsAndOutputs(int SlotCount)
  {
    try
    {
      for(int i=1;i<(SlotCount+1);i++) if(((BObject)get(inputPrefix+i))==null) this.add((inputPrefix+i), new BStatusString("", BStatus.nullStatus), Flags.SUMMARY);
      for(int i=1;i<(SlotCount+1);i++) if(((BObject)get(outputPrefix+i))==null) this.add((outputPrefix+i), new BStatusString("", BStatus.nullStatus), Flags.SUMMARY);
      
      for(int i=SlotCount+1;(BObject)get(inputPrefix+i)!=null;i++) if(((BObject)get(inputPrefix+i))!=null) this.remove(inputPrefix+i);
      for(int i=SlotCount+1;(BObject)get(outputPrefix+i)!=null;i++) if(((BObject)get(outputPrefix+i))!=null) this.remove(outputPrefix+i);
    }
    catch (Exception e)
    {
      logger.error(getSlotPath().toString() + e.getMessage());
      e.printStackTrace();
    }
    
    recreateBuffers(getNumberOfInputs(), getNumberOfBuffers());
  }
  
  private void recreateBuffers(int inputCount, int bufferCount)
  {
    try
    {
      for(int i=1;i<(bufferCount+1);i++)
      {
        for(int j=1;j<(inputCount+1);j++)
        {
          if(((BObject)get(bufferPrefix+i+bufferSlotDelimiter+j))==null) this.add((bufferPrefix+i+bufferSlotDelimiter+j), new BStatusString("", BStatus.nullStatus));
        }
      }
      
      for(int i=bufferCount+1;i < maxBuffers;i++)
      {
        for(int j=inputCount+1;j < maxInputs;j++)
        {
          if(((BObject)get(bufferPrefix+i+bufferSlotDelimiter+j))!=null) this.remove(bufferPrefix+i+bufferSlotDelimiter+j);
        }
      }
    }
    catch (Exception e)
    {
      logger.error(getSlotPath().toString() + e.getMessage());
      e.printStackTrace();
    }
  }
  public static final Log logger = Log.getLog(TYPE.getModule().getModuleName() + "." + TYPE.getTypeName());
}
