
package org.axcommunity.niagara.widgets;

import javax.baja.agent.*;
import javax.baja.gx.*;
import javax.baja.sys.*;
import javax.baja.ui.*;
import javax.baja.ui.event.*;
//import com.tridium.gx.awt.*;
//import com.tridium.sys.schema.*;
//import com.tridium.ui.theme.*;
import com.tridium.kitpx.enums.*;

/**
 * - BoundLabel with adjustable highlight colour for mouseOver
 * - Built on the framework that Andy Frank authored
 *   @author    Dean Mynott       - Ronin Control Systems Pty Ltd
 *   @creation  19 Nov 2011 
 */
 
public class BBoundLabelHighlight extends BLabel
{

////////////////////////////////////////////////////////////////
// Property "highLight"
////////////////////////////////////////////////////////////////
  public static final Property highLight = newProperty(0, BBrush.makeSolid(BColor.make(255,128,0,77)),null);    //#4dff8000
 // public static final Property highLight = newProperty(0, Theme.widget().getSelectionBackground(),null);  
  public BBrush getHighLight() { return (BBrush)get(highLight); }
  public void setHighLight(BBrush v) { set(highLight,v,null); }

////////////////////////////////////////////////////////////////
// Property "highLightBand"
////////////////////////////////////////////////////////////////
  public static final Property highLightBand = newProperty(0, 5,null);
  public int getHighLightBand() { return getInt(highLightBand); }
  public void setHighLightBand(int v) { setInt(highLightBand,v,null); }

////////////////////////////////////////////////////////////////
// Property "border"
////////////////////////////////////////////////////////////////
  public static final Property border = newProperty(0, BBorder.none,null);
  public BBorder getBorder() { return (BBorder)get(border); }
  public void setBorder(BBorder v) { set(border,v,null); }

////////////////////////////////////////////////////////////////
// Property "mouseOver"
////////////////////////////////////////////////////////////////
  public static final Property mouseOver = newProperty(0, BMouseOverEffect.highlight, null);
  public BMouseOverEffect getMouseOver() { return (BMouseOverEffect)get(mouseOver); }
  public void setMouseOver(BMouseOverEffect v) { set(mouseOver,v,null); }

////////////////////////////////////////////////////////////////
// Property "padding"
////////////////////////////////////////////////////////////////
  public static final Property padding = newProperty(0, BInsets.DEFAULT,null);
  public BInsets getPadding() { return (BInsets)get(padding); }
  public void setPadding(BInsets v) { set(padding,v,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BBoundLabelHighlight.class);

////////////////////////////////////////////////////////////////
// Icon
////////////////////////////////////////////////////////////////
  //icon for this component
  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/Ronin16.png"); 


////////////////////////////////////////////////////////////
// Constructor
////////////////////////////////////////////////////////////////
  public BBoundLabelHighlight(){ }
  
////////////////////////////////////////////////////////////////
// BWidget
////////////////////////////////////////////////////////////////
  //@Override
  public boolean receiveInputEvents()        // override super
  {
    return true;
  }
    
  public void paint(Graphics g)
  {
    double w = getWidth();
    double h = getHeight();
  
    if (!isMouseOver || getMouseOver() == BMouseOverEffect.none)
    {
      doPaint(g);
      return;    
    }
    
    switch (getMouseOver().getOrdinal())
    {
      case BMouseOverEffect.OUTLINE: 
        doPaint(g);
          g.setBrush( getHighLight() );
          g.setPen( BPen.make( getHighLightBand() ) ); 
          g.strokeRect(0,0,w-1,h-1); 
          break;      
      case BMouseOverEffect.HIGHLIGHT: 
        doPaint(g);
          g.setBrush( getHighLight() );
          g.fillRect(0,0,w-1,h-1);
          break;      
    }
  }

  private void doPaint(Graphics g)
  {
    super.paint(g);
    getBorder().paint(g, 1, 1, getWidth()-2, getHeight()-2);
  }

    
////////////////////////////////////////////////////////////////
// Mouse Events
////////////////////////////////////////////////////////////////
  public void mouseEntered(BMouseEvent event)
  { 
    isMouseOver = true;
    repaint();
  }
  
  public void mouseExited(BMouseEvent event)
  { 
    isMouseOver = false;
    repaint();
  }

////////////////////////////////////////////////////////////////
// Agents
////////////////////////////////////////////////////////////////
  public AgentList getAgents(Context cx)
  {
    AgentList agents = super.getAgents(cx);
    agents.toTop("kitPx:BoundLabelBinding");
    return agents;
  }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////
  private boolean isMouseOver = false;
}


