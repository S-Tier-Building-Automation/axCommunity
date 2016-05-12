package org.axcommunity.niagara.widgets;

import javax.baja.gx.BBrush;
import javax.baja.gx.BColor;
import javax.baja.gx.BFont;
import javax.baja.gx.Graphics;
import javax.baja.sys.*;
import javax.baja.ui.BWidget;

/**Label px widget that will rotate text that does not fit in the label.
 * One thing we had to do was use a kitPx:SetPointBinding instead of the traditional bajaui:ValueBinding because
 * we needed to detect value changes in code and valuebinding does not provide that interface.
 * 
 * As an added bonus, the object is capable of parsing delimited strings.  If you populate the parsedelim field with a string and
 * set the parseindex, it will retrieve the requested index field of the delim separated string input.
 *
 * @authors   Peter Tiagunov, Kors Engineering
 */
public class BRevolvingTextLabel extends BWidget
{
  /**input slot for data to display in label's text, pre-binded by default*/
  public static final Property text = newProperty(0, BString.DEFAULT,null);
  public String getText() { return getString(text); }
  public void setText(String v) { setString(text,v,null); }
  /**font to display label's text*/
  public static final Property font = newProperty(0, BFont.DEFAULT,null);//default = 12pt Sans-Serif
  public BFont getFont() { return (BFont)get(font); }
  public void setFont(BFont v) { set(font,v,null); }

  /**font color to display label's text*/
  //Brush to use to render the label's text.  If set to BBrush.NULL then a context sensitive default fallback will be used.
  public static final Property foreground = newProperty(0, BBrush.makeSolid(BColor.make(0x404040)),null);
  public BBrush getForeground() { return (BBrush)get(foreground); }
  public void setForeground(BBrush v) { set(foreground,v,null); }

  /**label default background color*/
  //Brush to fill background of label. Use BBrush.NULL to leave background transparent.
  public static final Property background = newProperty(0, BColor.make(0x80ff80).toBrush(),null);//primary color, default red
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
  
  /**output(optional) to propagate value to external boolean writable*/
  public static final Property boolOut = newProperty(0, false,null);
  public boolean getBoolOut() { return getBoolean(boolOut); }
  public void setBoolOut(boolean v) { setBoolean(boolOut,v,null); }

  //set icon
  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");

  //= layout ============================================================================
  public void computePreferredSize() //this method appears not to be invoked in Editor> To Preferred Size
  { setPreferredSize(120,20); }//typically match layout in module.palette
  
  public void doLayout(BWidget[] children) //method invoked by px viewer
  {
    w = getWidth();  //get width of widget's pane 
    h = getHeight(); //get height of widget's pane
    //control revolving text
    if(!getFont().isNull() && textlabel.length()>0) {
      ///System.out.println(BAbsTime.now().toString()+" Log Event (doLayout) textlabel ="+textlabel+" text width ="+getFont().width(textlabel)+", label width ="+w);
      //calculate if new textlabel value fits the pane using current font
      if(w!=0 && w<getFont().width(textlabel)) frameRevolve =0;//begin label's text revolving
      else {frameRevolve =-1; _textlabel = textlabel;}//disable label's text revolving
      }
    else frameRevolve =-1;//disable label's text revolving
  }
  //=====================================================================================
  public void paint(Graphics g)//note! all children e.g. fillRect and drawString are to be painted
  {
    if(!getFont().isNull()) {
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
        ///System.out.println(BAbsTime.now().getMinute()+":"+BAbsTime.now().getSecond()+":"+BAbsTime.now().getMillisecond()+" (paint) F0-lbl color:"+bcolor.asValue()+" Ffr:"+frameFlash+" Rfr:"+frameRevolve);
        g.fillRect(0,0,w,h);
      }
      //paint text
      if(!fcolor.isNull() && _textlabel != null){
      g.setBrush(fcolor);
      ///System.out.println(BAbsTime.now().getMinute()+":"+BAbsTime.now().getSecond()+":"+BAbsTime.now().getMillisecond()+" (paint) F0-txt color:"+fcolor.asValue()+" Ffr:"+frameFlash+" Rfr:"+frameRevolve+" "+_textlabel);
      g.drawString(_textlabel, Math.ceil(fontHeight/2), Math.ceil(h/2+fontHeight/2));}
     }
    else//paint flashing phase, if null, then all transparent
    {
      if(acolor.isNull() && fcolor.isNull()) {
        ///System.out.println(BAbsTime.now().getMinute()+":"+BAbsTime.now().getSecond()+":"+BAbsTime.now().getMillisecond()+" (paint) F1-lbl color:"+acolor.asValue()+" Ffr:"+frameFlash+" Rfr:"+frameRevolve);
        ///System.out.println(BAbsTime.now().getMinute()+":"+BAbsTime.now().getSecond()+":"+BAbsTime.now().getMillisecond()+" (paint) F1-txt color:"+fcolor.asValue()+" Ffr:"+frameFlash+" Rfr:"+frameRevolve+" "+_textlabel);
        return;}//paint all transparent
      else//either flash color and/or font color not null(s)
      { //paint label to flash color
        if(acolor.isNull()) { g.setBrush(BColor.transparent); }
        else { g.setBrush(acolor); }
        ///System.out.println(BAbsTime.now().getMinute()+":"+BAbsTime.now().getSecond()+":"+BAbsTime.now().getMillisecond()+" (paint) F1-lbl color:"+acolor.asValue()+" fFr:"+frameFlash+" Rfr:"+frameRevolve);
        g.fillRect(0,0,w,h);//}
        //paint text
        if(fcolor.isNull()) { g.setBrush(BColor.transparent); }
        else { g.setBrush(fcolor); }
        ///System.out.println(BAbsTime.now().getMinute()+":"+BAbsTime.now().getSecond()+":"+BAbsTime.now().getMillisecond()+" (paint) F1-txt color:"+fcolor.asValue()+" Ffr:"+frameFlash+" Rfr:"+frameRevolve+" "+_textlabel);
        g.drawString(_textlabel, Math.ceil(fontHeight/2), Math.ceil(h/2+fontHeight/2));//}
      }
    }
    }
  }
  //Animate is the callback invoked at a standard frame rate of 10/sec (once every 100ms)
  //The default implementation paints the children widgets
  //=====================================================================================
  public void animate()//in px view mode only
  {          
   if(getFlash())//if flash enable
   {        
     frameFlash =(frameFlash+1) % 10;//increment by 1 from 0 to 9
     //System.out.println(BAbsTime.now().toString()+" Log Event (animate), Frame:"+frameFlash);
     //if (frameFlash == 0 || frameFlash == 7) repaint();//repaint only on 0th or 7th (transitional) frame(s) 
   }
       
   if(frameRevolve>=0) {
     frameRevolve =(frameRevolve+1) % (20+textlabel.length()+3);//initial 2000 ms delay +nr of char(s) to revolve +3 trailing characters
     //System.out.println(BAbsTime.now().toString()+" Log Event (animate.revolve), Frame:"+frameFlash);
     if(frameRevolve>20)//begin label's text revolving
     {
       _textlabel =_textlabel.substring(1)+_textlabel.substring(0, 1);
       ///System.out.println(BAbsTime.now().toString()+" Log Event (animate.revolve), Frame:"+frameRevolve+ " "+_textlabel);
     }
     else _textlabel =textlabel+"...";//set revolving value with suffix
   }
   else _textlabel =textlabel;//set revolving value as is
  
   if((getFlash() && frameFlash ==0 || frameFlash ==7) || (frameRevolve ==1 || frameRevolve >=20))//synchronize flash and/or text revolve repaints
   {
     repaint();//repaint all children
   }
  }
  //=====================================================================================
  //this method automatically? invokes async. paint() and layout() method(s), if available
  //for binded slot(s), applicable to kitPx binding(s) only
  public void changed (Property prop, Context context)//invoked in both, edit widget property & px view mode(s)
  {
    super.changed(prop, context);
    ///System.out.println(BAbsTime.now().toString()+" Log Event (changed) prop :"+prop.getName().toString());
    if(prop ==flash || prop ==visible) frameFlash=0;//set default first repaintable frame, then repaint
    
    if(prop ==text || prop ==font)
    { //validate if enabled, parse text property;
      if (getParseindex() > 0 && getText().toString()!="" && getParsedelim().toString().length()==1)//getDelimiter().getValue().toString()!="" && !getDelimiter().isNull())
      {
        ///System.out.println(BAbsTime.now().toString()+" Log Event (changed) prop :"+prop.getName().toString()+" value:"+getParsedelim().toString());
        textlabel = parse(getText().toString(), getParseindex(), getParsedelim().toString());
      }
      else textlabel = getText().toString();
    }
    if(textlabel =="") {setBoolOut(false);} ///System.out.println("FALSE");} 
    else {setBoolOut(true);} ///System.out.println("TRUE");}
    //setBoolOut(new BStatusBoolean(false))
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
    ///System.out.println(BAbsTime.now().toString()+" first delimiter at index: "+delimIndex);
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
          ///System.out.println(BAbsTime.now().toString()+" i= "+i+" beginIndex= "+beginIndex+" endIndex= "+endIndex);
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
  
  public static final Type TYPE = Sys.loadType(BRevolvingTextLabel.class);
  public Type getType() { return TYPE; }
  
  int frameFlash = 0;   // 0-6 on, 7-9 off
  int frameRevolve =-1;
  double w =getWidth();
  double h =getHeight();
  String textlabel ="";
  String _textlabel ="";
  BFont _font =getFont();//get font from font property
  String _parsedelim;

}
