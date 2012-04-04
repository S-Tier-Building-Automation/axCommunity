package org.axcommunity.niagara.views;


import javax.baja.gx.BSize;
import javax.baja.sys.Action;
import javax.baja.sys.BBoolean;
import javax.baja.sys.BObject;
import javax.baja.sys.BValue;
import javax.baja.sys.Context;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.ui.BButton;
import javax.baja.ui.enums.BScaleMode;
import javax.baja.ui.event.BMouseEvent;

import javax.baja.ui.pane.BCanvasPane;

import javax.baja.workbench.view.*;

import org.axcommunity.niagara.logic.BSetBooleanAction;


public class BSetBooleanActionView
    extends BWbComponentView
{
  
  protected BSetBooleanAction sbAction = null;

  public BSetBooleanActionView()
  {
  }

  public void doLoadValue(BObject obj, Context cx)
  {
    BButton test = new BButton("test");
    //cast incoming obj to a BSetBooleanAction object    
    sbAction = (BSetBooleanAction)obj;
  
    //create the background canvas the objects will show up on
    BCanvasPane canvas = new BCanvasPane();
    canvas.setViewSize(BSize.make(80,20));
    canvas.setScale(BScaleMode.fitRatio);
    
    //for now just use the object display name as the button label
    test.setText(sbAction.getDisplayName(cx));
    canvas.add(null,test);
    
    //link the mouse events of the button to the handler of the view
    linkTo(null,test,BButton.mouseEvent,handleMouseEvent);
    
    setContent(canvas);
   }


  public static final Action handleMouseEvent = newAction(0,new BMouseEvent());
  public void handleMouseEvent(BMouseEvent arg) { invoke(handleMouseEvent,arg,null); }
  public void doHandleMouseEvent(BMouseEvent event)
  {
    switch (event.getId())
    {
      case BMouseEvent.MOUSE_PRESSED:
        //on click, invoke the set true action
        sbAction.invoke(sbAction.getAction("SetTrue"), (BValue)BBoolean.make(true));
        break;
      case BMouseEvent.MOUSE_RELEASED:
        //on release, invoke the set false action
        sbAction.invoke(sbAction.getAction("SetFalse"), (BValue)BBoolean.make(true));
        break;
      case BMouseEvent.MOUSE_PULSED:
        //not sure how to use this one.  could be handy though...
        break;
    }
  }
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BSetBooleanActionView.class);


}