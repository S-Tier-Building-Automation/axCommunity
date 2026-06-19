package org.axcommunity.niagara.conversion;

import javax.baja.file.BIFile;
import javax.baja.gx.BImage;
import javax.baja.naming.BOrd;
import javax.baja.status.BStatusString;
import javax.baja.sys.BComponent;
import javax.baja.sys.BIcon;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
/** Input is a file ord pointing to an image file: example "file:^Images/Image1.jpg"
 *  Output is a BImage that can be used to animate any widget with an "Image" property
 *  To use, add a value binding to your widget, point it to the outImage property of this object
 *  In the widget, animate the Image property, make sure to set the Converter Type to "Pass Through"
*/
public class BFileToImage
    extends BComponent
{
  /**Input string with file ord*/
  public static final Property inFileOrd = newProperty(Flags.SUMMARY, new BStatusString(""));
  public BStatusString getInFileOrd() { return (BStatusString)get(inFileOrd);}
  public void setInFileOrd(BStatusString v) {set(inFileOrd,v);}
  /**Output image*/
  public static final Property outImage = newProperty(Flags.SUMMARY, BImage.DEFAULT);
  public BImage getOutImage() { return (BImage)get(outImage);}
  public void setOutImage(BImage v) {set(outImage,v);}

  public void changed(Property property, Context context){
    super.changed(property, context);
    if(!Sys.atSteadyState() || !isRunning()){
    return;
    }
    try
    {
      BIFile file = (BIFile)BOrd.make(getInFileOrd().getValue()).get();
      //byte[] imgText = new byte[0];
      //imgText = file.read();
      //BImage img = BImage.make(imgText);
      BOrd ord = BOrd.make(getInFileOrd().getValue());
      setOutImage(BImage.make(ord));
      }
    catch (Exception e)
    {
      // TODO Auto-generated catch block
      setOutImage(BImage.DEFAULT);
      e.printStackTrace();
    }
  }
  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");
      
 public static final Type TYPE = Sys.loadType(BFileToImage.class);
 public Type getType() { return TYPE; }   

    
}
