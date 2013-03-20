package org.axcommunity.niagara.widgets;

import javax.baja.gx.BBrush;
import javax.baja.gx.BColor;
import javax.baja.gx.BFont;
import javax.baja.gx.Graphics;
import javax.baja.sys.BIcon;
import javax.baja.sys.BString;
import javax.baja.sys.Context;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.ui.BWidget;


/**Label px widget that will rotate text that does not fit in the label.
* One thing we had to do was use a kitPx:SetPointBinding instead of the traditional bajaui:ValueBinding because
* we needed to detect value changes in code and valuebinding does not provide that interface.
* 
 * As an added bonus, the object is capable of parsing delimited strings.  If you populate the parsedelim field with a string and
* set the parseindex, it will retrieve the requested index field of the delim separated string input.
*
* @authors   Peter Tiagunov, Kors Engineering
*            Eric Bishop, Texas Machining Technologies
*            
* Added features:
*       Variable scroll speed
*       Custom text at end of string
*       Enable/disable pause at begining of string
*       
* Fixes:
*       Sets prefered font size properly
*       Sets default font properly
*       Removed custom default background & foregound
*/
public class BRevolvingTextLabel2 extends BWidget
{
  /**input slot for data to display in label's text, pre-binded by default*/
  public static final Property text = newProperty(0, BString.DEFAULT,null);
  public String getText() { return getString(text); }
  public void setText(String v) { setString(text,v,null); }

  /**font to display label's text*/
  public static final Property font = newProperty(0, BFont.NULL,null);//default = 12pt Sans-Serif
  public BFont getFont() { return (BFont)get(font); }
  public void setFont(BFont v) { set(font,v,null); }

  /**font color to display label's text*/
  //Brush to use to render the label's text.  If set to BBrush.NULL then a context sensitive default fallback will be used.
  public static final Property foreground = newProperty(0, BBrush.NULL,null);
  public BBrush getForeground() { return (BBrush)get(foreground); }
  public void setForeground(BBrush v) { set(foreground,v,null); }

  /**label default background color*/
  //Brush to fill background of label. Use BBrush.NULL to leave background transparent.
  public static final Property background = newProperty(0, BBrush.NULL,null);
  public BBrush getBackground() { return (BBrush)get(background); }
  public void setBackground(BBrush v) { set(background,v,null); }

  /**label alternative background color, while flashing*/
  public static final Property flashcolor = newProperty(0, BColor.NULL.toBrush(),null);//flashing color, default transparent
  public BBrush getFlashcolor() { return (BBrush)get(flashcolor); }
  public void setFlashcolor(BBrush v) { set(flashcolor,v,null); }

  //Boolean. If flash it true then the label is flashed on and off using flashcolor
  /**flash control, pre-binded by default*/
  public static final Property flash = newProperty(0, false,null);
  public boolean getFlash() { return getBoolean(flash); }
  public void setFlash(boolean v) { setBoolean(flash,v,null); }

  /**parse control, index of element in data to display in label's text, Default =-1 (disabled)*/
  public static final Property parseindex = newProperty(0, -1,null);
  public int getParseindex() { return getInt(parseindex); }
  public void setParseindex(int v) { setInt(parseindex,v,null); }

  /**parse delimiter, default ="", applicable: "," -comma, "|" -pipe, etc.*/
  public static final Property parsedelim = newProperty(0, BString.DEFAULT,null);
  public String getParsedelim() { return getString(parsedelim); }
  public void setParsedelim(String v) { setString(parsedelim,v,null); }

  /**output(optional) to propagate value to external boolean writable.  If output is blank, returns false.  Otherwise, returns true.*/
  public static final Property boolOut = newProperty(0, false,null);
  public boolean getBoolOut() { return getBoolean(boolOut); }
  public void setBoolOut(boolean v) { setBoolean(boolOut,v,null); }

  public static final Property textSpeed = newProperty(0, 0,null);
  public int getTextSpeed() { return getInt(textSpeed); }
  public void setTextSpeed(int v) { setInt(textSpeed,v,null); }

  public static final Property pauseAtBegining = newProperty(0, true,null);
  public boolean getPauseAtBegining() { return getBoolean(pauseAtBegining); }
  public void setPauseAtBegining(boolean v) { setBoolean(pauseAtBegining,v,null); }
  
  public static final Property endStringRevolutionWith = newProperty(0, "...",null);
  public String getEndStringRevolutionWith() { return getString(endStringRevolutionWith); }
  public void setEndStringRevolutionWith(String v) { setString(endStringRevolutionWith,v,null); }
  
  //set icon
  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");

  //= layout ============================================================================
  public void computePreferredSize()
  {
    //setting the preferred height allows the text box to expand automatically if the font size is increased in certain panes. 
    double fontHeight = getFont().getHeight();
    setPreferredSize(120, fontHeight);
  }

  public void doLayout(BWidget[] children) //method invoked by px viewer
  {
    w = getWidth();  //get width of widget's pane 
    h = getHeight(); //get height of widget's pane
    //control revolving text
    if(!getFont().isNull() && textlabel.length()>0)
    {
      //calculate if new textlabel value fits the pane using current font
      if(w!=0 && w<getFont().width(textlabel)) frameRevolve =0;//begin label's text revolving
      else {frameRevolve =-1; _textlabel = textlabel;}//disable label's text revolving
    }
    else frameRevolve =-1;//disable label's text revolving
  }
  //=====================================================================================
  public void paint(Graphics g)//note! all children e.g. fillRect and drawString are to be painted
  {
    if(!getFont().isNull())
    {
      _font =getFont();
      g.setFont(_font);
      double fontHeight = (_font.getDescent()+_font.getAscent())/2;
      BBrush bcolor =getBackground();
      BBrush acolor =getFlashcolor();
      BBrush fcolor =getForeground();

      if(frameFlash < 7)//paint non-flashing or default phase
      { //paint label to primary color
        if(!bcolor.isNull())
        {
          g.setBrush(bcolor);
          g.fillRect(0,0,w,h);
        }
        //paint text
        if(!fcolor.isNull() && _textlabel != null)
        {
          g.setBrush(fcolor);
          g.drawString(_textlabel, Math.ceil(fontHeight/4), Math.ceil(h/2+fontHeight/2));
        }
      }
      else//paint flashing phase, if null, then all transparent
      {
        if(acolor.isNull() && fcolor.isNull()) return;//paint all transparent
        else//either flash color and/or font color not null(s)
        { //paint label to flash color
          if(acolor.isNull()) g.setBrush(BColor.transparent);
          else g.setBrush(acolor);
          g.fillRect(0,0,w,h);
          //paint text
          if(fcolor.isNull())g.setBrush(BColor.transparent);
          else g.setBrush(fcolor);
          g.drawString(_textlabel, Math.ceil(fontHeight/4), Math.ceil(h/2+fontHeight/2));
        }
      }
    }
  }
  //Animate is the callback invoked at a standard frame rate of 10/sec (once every 100ms)
  //The default implementation paints the children widgets
  //=====================================================================================
  public void animate()//in px view mode only
  {
    //if (frameFlash == 0 || frameFlash == 7) repaint();//repaint only on 0th or 7th (transitional) frame(s)
    //if flash enable
    if(getFlash()) frameFlash =(frameFlash+1) % 10;//increment by 1 from 0 to 9

    if(frameRevolve>=0)
    {
      //Skip the delay if the delay is disabled
      if(!getPauseAtBegining() && frameRevolve<20) frameRevolve = 20;
      
      if(frameRevolve<=20)
      {
        //First 20 passes are being processed (2 second delay upon start). Set revolving value with suffix
        frameRevolve =(frameRevolve+1) % (20+textlabel.length()+getEndStringRevolutionWith().length());
        _textlabel =textlabel+getEndStringRevolutionWith();
      }
      
      //Initial 2 second delay completed
      else
      {
        //see if it is time to advance one character
        if(textSpeedCycleCount >= 0)
        {
          int characterOffset = 0;
          if(getTextSpeed() > 0) characterOffset = getTextSpeed();
          textSpeedCycleCount = getTextSpeed();
          
          //initial 2000 ms delay +nr of char(s) to revolve +3 trailing characters
          frameRevolve =(frameRevolve+1+characterOffset) % (20+textlabel.length()+getEndStringRevolutionWith().length());
          if(frameRevolve>20) _textlabel =_textlabel.substring(1+characterOffset)+_textlabel.substring(0, 1+characterOffset);
        }
        
        //it isn't time to advance one character yet
        else textSpeedCycleCount++;
      }
    }
    
    //Text too short, revolving disabled. Set revolving value as is
    else _textlabel =textlabel;

    //synchronize flash and/or text revolve repaints
    if((getFlash() && frameFlash ==0 || frameFlash ==7) || (frameRevolve ==1 || frameRevolve >=20)) repaint();//repaint all children
  }
  //=====================================================================================
  //this method automatically? invokes async. paint() and layout() method(s), if available
  //for binded slot(s), applicable to kitPx binding(s) only
  public void changed (Property prop, Context context)//invoked in both, edit widget property & px view mode(s)
  {
    super.changed(prop, context);
    if(prop ==flash || prop ==visible) frameFlash=0;//set default first repaintable frame, then repaint

    if(prop ==text || prop ==font)
    { //validate if enabled, parse text property;
      if (getParseindex() > 0 && getText().toString()!="" && getParsedelim().toString().length()==1) textlabel = parse(getText().toString(), getParseindex(), getParsedelim().toString());
      else textlabel = getText().toString();
    }
    if(textlabel =="") setBoolOut(false); 
    else setBoolOut(true);
  }
  //= parse n-th element from delimited string ==========================================
  public String parse(String s, int n, String d)
  {
    ///System.out.println(BAbsTime.now().toString()+" s="+s+" n="+n+" d="+d+".");
    int beginIndex  =0;
    int delimIndex  =0;
    int endIndex    =0;
    int eos         =0;
    int i           =0;
    String output   ="";

    delimIndex =s.indexOf(d, beginIndex);//get index of the first delimiter, if not found, returns -1
    if(s.indexOf(d, beginIndex)>=0)
    {
      while(i<n && eos<=0)
      {
        //get index of the current delimiter
        delimIndex =s.indexOf(d, beginIndex);
        if(delimIndex < 0)//exception: the delimiter is not found or current element is the last
        {
          endIndex =s.length();//set endIndex to the last character in the string
          eos =-1;//the end of string is reached
        }
        else endIndex =delimIndex; //the delimiter is found, get endIndex

        //if n-th element exists, parse it to output
        if(i==(n-1)) output =s.substring(beginIndex, endIndex);
        if(eos ==-1) eos=1;//break while
        beginIndex =delimIndex+1;//move to next element
        i++;
      }
    }
    else output ="n/a";//no delimiter found

    return output;
  }

  //=====================================================================================
  public boolean receiveInputEvents()//in px view mode only
  {
    //System.out.println(BAbsTime.now().toString()+" Log Event (receiveInputEvents)");
    return hasBindings();
  }

  public static final Type TYPE = Sys.loadType(BRevolvingTextLabel2.class);
  public Type getType() { return TYPE; }

  int textSpeedCycleCount = 0;
  int frameFlash = 0;   // 0-6 on, 7-9 off
  
  /**
   * -1       = revolve off<br>
   *  0 to 20 = Initial pause<br>
   * 20 to 20+textlabel.length() = Scrolling Text<br>
   * 20+textlabel.length() to 20+textlabel.length()+3 = elipses at end of string
   * */
  int frameRevolve =-1;
  double w =getWidth();
  double h =getHeight();
  String textlabel ="";
  String _textlabel ="";
  BFont _font =getFont();//get font from font property
  String _parsedelim;

}



