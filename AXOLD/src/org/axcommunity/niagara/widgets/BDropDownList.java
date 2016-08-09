package org.axcommunity.niagara.widgets;

import javax.baja.naming.BOrd;
import javax.baja.sys.BIcon;
import javax.baja.sys.Context;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.ui.BIHyperlinkShell;
import javax.baja.ui.BLabel;
import javax.baja.ui.BLayout;
import javax.baja.ui.BMenu;
import javax.baja.ui.BWidget;
import javax.baja.ui.BWidgetShell;
import javax.baja.ui.Command;
import javax.baja.ui.CommandArtifact;
import javax.baja.ui.HyperlinkInfo;
import javax.baja.ui.MouseCursor;
import javax.baja.ui.event.BMouseEvent;

public class BDropDownList
    extends BLabel
{

  
////////////////////////////////////////////////////////////////
//Label Command 1
////////////////////////////////////////////////////////////////
                class LinkToCommand01 
                      extends Command

                {
                public CommandArtifact doInvoke()
                throws Exception
                {               
                  if(getShell() != null)
                {
                  BOrd bord = getLinkTo01();
                  if(!bord.isNull())
                  ((BIHyperlinkShell)getShell()).hyperlink(new HyperlinkInfo(bord));
    
                }
                 return null;
                }
                  BDropDownList thislabel;
                  LinkToCommand01(BWidget bwidget)
                {
                  super(bwidget, getLabel01());
                }

                }
                
////////////////////////////////////////////////////////////////
//Label 1
////////////////////////////////////////////////////////////////

                public BOrd getLinkTo01()
                {
                  return (BOrd)get(linkTo01);
                }

                public void setLinkTo01(BOrd bord)
                {
                  set(linkTo01, bord, null);
                }

                public String getLabel01()
                {
                  return getString(Label01);
                }

                public void setLabel01(String s)
                {
                  setString(Label01, s, null);
                }       
     
////////////////////////////////////////////////////////////////
//Label Command 2
////////////////////////////////////////////////////////////////
                class LinkToCommand02 
                extends Command

                {
                public CommandArtifact doInvoke()
                throws Exception
                {          
                  if(getShell() != null)
                {
                  BOrd bord = getLinkTo02();
                  if(!bord.isNull())
                  ((BIHyperlinkShell)getShell()).hyperlink(new HyperlinkInfo(bord));

                }
                  return null;
                }
                  BDropDownList thislabel02;
                  LinkToCommand02(BWidget bwidget)
                {
                  super(bwidget, getLabel02());
                }

                }
                
////////////////////////////////////////////////////////////////
//Label 2
////////////////////////////////////////////////////////////////

                public BOrd getLinkTo02()
                {
                  return (BOrd)get(linkTo02);
                }

                public void setLinkTo02(BOrd bord)
                {
                  set(linkTo02, bord, null);
                }

                public String getLabel02()
                {
                  return getString(Label02);
                }

                public void setLabel02(String s)
                {
                  setString(Label02, s, null);
                }
                          
////////////////////////////////////////////////////////////////
//Label Command 3
////////////////////////////////////////////////////////////////
                class LinkToCommand03
                extends Command

                {
                  public CommandArtifact doInvoke()
                  throws Exception
                {               
                  if(getShell() != null)
                {
                  BOrd bord = getLinkTo03();
                  if(!bord.isNull())
                  ((BIHyperlinkShell)getShell()).hyperlink(new HyperlinkInfo(bord));

                }
                  return null;
                }
                  BDropDownList thisLabel03;
                  LinkToCommand03(BWidget bwidget)
                {
                  super(bwidget, getLabel03());
                }

                }
                
////////////////////////////////////////////////////////////////
//Label 3
////////////////////////////////////////////////////////////////

                public BOrd getLinkTo03()
                {
                  return (BOrd)get(linkTo03);
                }

                public void setLinkTo03(BOrd bord)
                {
                  set(linkTo03, bord, null);
                }

                public String getLabel03()
                {
                  return getString(Label03);
                }

                public void setLabel03(String s)
                {
                  setString(Label03, s, null);
                }
                
////////////////////////////////////////////////////////////////
//Label Command 4
////////////////////////////////////////////////////////////////
                              
                class LinkToCommand04
                extends Command

                {
                  public CommandArtifact doInvoke()
                  throws Exception
                {               
                  if(getShell() != null)
                {
                  BOrd bord = getLinkTo04();
                  if(!bord.isNull())
                ((BIHyperlinkShell)getShell()).hyperlink(new HyperlinkInfo(bord));

                }
                  return null;
                }
                  BDropDownList thisLabel04;
                  LinkToCommand04(BWidget bwidget)
                {
                  super(bwidget, getLabel04());
                }

                }
                
////////////////////////////////////////////////////////////////
//Label 4
////////////////////////////////////////////////////////////////
                
                public BOrd getLinkTo04()
                {
                  return (BOrd)get(linkTo04);
                }

                public void setLinkTo04(BOrd bord)
                {
                  set(linkTo04, bord, null);
                }
                              
                public String getLabel04()
                {
                  return getString(Label04);
                }

                public void setLabel04(String s)
                {
                  setString(Label04, s, null);
                }
                      
////////////////////////////////////////////////////////////////
//Label Command 5
////////////////////////////////////////////////////////////////
                
                class LinkToCommand05
                extends Command

                {
                public CommandArtifact doInvoke()
                throws Exception
                {               
                  if(getShell() != null)
                {
                BOrd bord = getLinkTo05();
                  if(!bord.isNull())
                ((BIHyperlinkShell)getShell()).hyperlink(new HyperlinkInfo(bord));

                }
                  return null;
                }
                  BDropDownList thisLabel06;
                  LinkToCommand05(BWidget bwidget)
                {
                  super(bwidget, getLabel05());
                }

                }
                
////////////////////////////////////////////////////////////////
//Label 5
////////////////////////////////////////////////////////////////

                public BOrd getLinkTo05()
                {
                  return (BOrd)get(linkTo05);
                }

                public void setLinkTo05(BOrd bord)
                {
                  set(linkTo05, bord, null);
                }
                                            
                public String getLabel05()
                {
                  return getString(Label05);
                }

                public void setLabel05(String s)
                {
                  setString(Label05, s, null);
                }
                
////////////////////////////////////////////////////////////////
//Label Command 6
////////////////////////////////////////////////////////////////
                              
                class LinkToCommand06
                extends Command

                {
                  public CommandArtifact doInvoke()
                  throws Exception
                {               
                  if(getShell() != null)
                {
                  BOrd bord = getLinkTo06();
                  if(!bord.isNull())
                  ((BIHyperlinkShell)getShell()).hyperlink(new HyperlinkInfo(bord));

                }
                  return null;
                }
                  BDropDownList thislabel06;
                  LinkToCommand06(BWidget bwidget)
                  {
                    super(bwidget, getLabel06());
                  }

                }
                
////////////////////////////////////////////////////////////////
//Label 6
////////////////////////////////////////////////////////////////

                public BOrd getLinkTo06()
                {
                  return (BOrd)get(linkTo06);
                }

                public void setLinkTo06(BOrd bord)
                {
                  set(linkTo06, bord, null);
                }
                                                          
                public String getLabel06()
                {
                  return getString(Label06);
                }

                public void setLabel06(String s)
                {
                  setString(Label06, s, null);
                }
                
////////////////////////////////////////////////////////////////
//Label Command 7
////////////////////////////////////////////////////////////////
                                            
                class LinkToCommand07
                extends Command

                {
                  public CommandArtifact doInvoke()
                  throws Exception
                {               
                  if(getShell() != null)
                {
                  BOrd bord = getLinkTo07();
                  if(!bord.isNull())
                  ((BIHyperlinkShell)getShell()).hyperlink(new HyperlinkInfo(bord));

                }
                  return null;
                }
                  BDropDownList thisLabel07;
                  LinkToCommand07(BWidget bwidget)
                {
                  super(bwidget, getLabel07());
                }

                }
                              
////////////////////////////////////////////////////////////////
//Label 7
////////////////////////////////////////////////////////////////

                public BOrd getLinkTo07()
                {
                  return (BOrd)get(linkTo07);
                }

                public void setLinkTo07(BOrd bord)
                {
                  set(linkTo07, bord, null);
                }
                                                                        
                public String getLabel07()
                {
                  return getString(Label07);
                }

                public void setLabel07(String s)
                {
                  setString(Label07, s, null);
                }
                
////////////////////////////////////////////////////////////////
//Label Command 8
////////////////////////////////////////////////////////////////
                                                          
                class LinkToCommand08
                extends Command

                {
                  public CommandArtifact doInvoke()
                  throws Exception
                {               
                  if(getShell() != null)
                {
                  BOrd bord = getLinkTo08();
                  if(!bord.isNull())
                  ((BIHyperlinkShell)getShell()).hyperlink(new HyperlinkInfo(bord));

                }
                  return null;
                }
                  BDropDownList thisLabel08;
                  LinkToCommand08(BWidget bwidget)
                {
                  super(bwidget, getLabel08());
                }

                }
                                            
////////////////////////////////////////////////////////////////
//Label 8
////////////////////////////////////////////////////////////////

                public BOrd getLinkTo08()
                {
                  return (BOrd)get(linkTo08);
                }

                public void setLinkTo08(BOrd bord)
                {
                  set(linkTo08, bord, null);
                }
                                                                                      
                public String getLabel08()
                {
                  return getString(Label08);
                }

                public void setLabel08(String s)
                {
                  setString(Label08, s, null);
                }
                
////////////////////////////////////////////////////////////////
//Label Command 9
////////////////////////////////////////////////////////////////
                                                                        
                class LinkToCommand09
                extends Command

                {
                  public CommandArtifact doInvoke()
                  throws Exception
                {               
                  if(getShell() != null)
                {
                  BOrd bord = getLinkTo09();
                  if(!bord.isNull())
                  ((BIHyperlinkShell)getShell()).hyperlink(new HyperlinkInfo(bord));

                }
                  return null;
                }
                  BDropDownList thisLabel09;
                  LinkToCommand09(BWidget bwidget)
                {
                  super(bwidget, getLabel09());
                }

                }
                                                          
////////////////////////////////////////////////////////////////
//Label 9
////////////////////////////////////////////////////////////////

                public BOrd getLinkTo09()
                {
                  return (BOrd)get(linkTo09);
                }

                public void setLinkTo09(BOrd bord)
                {
                  set(linkTo09, bord, null);
                }
                                                                                                    
                public String getLabel09()
                {
                  return getString(Label09);
                }

                public void setLabel09(String s)
                {
                  setString(Label09, s, null);
                }
                
////////////////////////////////////////////////////////////////
//Label Command 10
////////////////////////////////////////////////////////////////
                                                                                      
                class LinkToCommand10
                extends Command

                {
                  public CommandArtifact doInvoke()
                  throws Exception
                {               
                  if(getShell() != null)
                {
                  BOrd bord = getLinkTo10();
                  if(!bord.isNull())
                  ((BIHyperlinkShell)getShell()).hyperlink(new HyperlinkInfo(bord));

                }
                  return null;
                }
                  BDropDownList thislabel10;
                  LinkToCommand10(BWidget bwidget)
                {
                  super(bwidget, getLabel10());
                }

                }
                                                                        
////////////////////////////////////////////////////////////////
//Label 10
////////////////////////////////////////////////////////////////

                public BOrd getLinkTo10()
                {
                  return (BOrd)get(linkTo10);
                }

                public void setLinkTo10(BOrd bord)
                {
                  set(linkTo10, bord, null);
                }
                                                                                                                  
                public String getLabel10()
                {
                  return getString(Label10);
                }

                public void setLabel10(String s)
                {
                  setString(Label10, s, null);
                }
  
////////////////////////////////////////////////////////////////
//Label Command 11
////////////////////////////////////////////////////////////////
                                                                                                    
                class LinkToCommand11
                extends Command

                {
                  public CommandArtifact doInvoke()
                  throws Exception
                {               
                  if(getShell() != null)
                {
                  BOrd bord = getLinkTo11();
                  if(!bord.isNull())
                  ((BIHyperlinkShell)getShell()).hyperlink(new HyperlinkInfo(bord));

                }
                  return null;
                }
                  BDropDownList thislabel11;
                  LinkToCommand11(BWidget bwidget)
                {
                  super(bwidget, getLabel11());
                }

                }
                                                                                      
////////////////////////////////////////////////////////////////
//Label 11
////////////////////////////////////////////////////////////////

                public BOrd getLinkTo11()
                {
                  return (BOrd)get(linkTo11);
                }

                public void setLinkTo11(BOrd bord)
                {
                  set(linkTo11, bord, null);
                }
                                                                                                                                
                public String getLabel11()
                {
                  return getString(Label11);
                }

                public void setLabel11(String s)
                {
                  setString(Label11, s, null);
                }
                
////////////////////////////////////////////////////////////////
//Label Command 12
////////////////////////////////////////////////////////////////
                                                                                                                  
                class LinkToCommand12
                extends Command

                {
                  public CommandArtifact doInvoke()
                  throws Exception
                {               
                  if(getShell() != null)
                {
                  BOrd bord = getLinkTo12();
                  if(!bord.isNull())
                  ((BIHyperlinkShell)getShell()).hyperlink(new HyperlinkInfo(bord));

                }
                  return null;
                }
                  BDropDownList thislabel12;
                  LinkToCommand12(BWidget bwidget)
                {
                  super(bwidget, getLabel12());
                }

                }
                                                                                                    
////////////////////////////////////////////////////////////////
//Label 12
////////////////////////////////////////////////////////////////

                public BOrd getLinkTo12()
                {
                  return (BOrd)get(linkTo12);
                }

                public void setLinkTo12(BOrd bord)
                {
                  set(linkTo12, bord, null);
                }
                                                                                                                                              
                public String getLabel12()
                {
                  return getString(Label12);
                }

                public void setLabel12(String s)
                {
                  setString(Label12, s, null);
                }
                
////////////////////////////////////////////////////////////////
//Label Command 13
////////////////////////////////////////////////////////////////
                                                                                                                                
                class LinkToCommand13
                extends Command

                {
                  public CommandArtifact doInvoke()
                  throws Exception
                {               
                  if(getShell() != null)
                {
                  BOrd bord = getLinkTo13();
                  if(!bord.isNull())
                  ((BIHyperlinkShell)getShell()).hyperlink(new HyperlinkInfo(bord));

                }
                  return null;
                }
                  BDropDownList thislabel13;
                  LinkToCommand13(BWidget bwidget)
                {
                  super(bwidget, getLabel13());
                }

                }
                                                                                                                  
////////////////////////////////////////////////////////////////
//Label 13
////////////////////////////////////////////////////////////////

                public BOrd getLinkTo13()
                {
                  return (BOrd)get(linkTo13);
                }

                public void setLinkTo13(BOrd bord)
                {
                  set(linkTo13, bord, null);
                }
                                                                                                                                                            
                public String getLabel13()
                {
                  return getString(Label13);
                }

                public void setLabel13(String s)
                {
                  setString(Label13, s, null);
                }
                
////////////////////////////////////////////////////////////////
//Label Command 14
////////////////////////////////////////////////////////////////
                                                                                                                                              
                class LinkToCommand14
                extends Command

                {
                  public CommandArtifact doInvoke()
                  throws Exception
                {               
                  if(getShell() != null)
                {
                  BOrd bord = getLinkTo14();
                  if(!bord.isNull())
                  ((BIHyperlinkShell)getShell()).hyperlink(new HyperlinkInfo(bord));

                }
                  return null;
                }
                  BDropDownList thislabel14;
                  LinkToCommand14(BWidget bwidget)
                {
                  super(bwidget, getLabel14());
                }

                }
                                                                                                                                
////////////////////////////////////////////////////////////////
//Label 14
////////////////////////////////////////////////////////////////

                public BOrd getLinkTo14()
                {
                  return (BOrd)get(linkTo14);
                }

                public void setLinkTo14(BOrd bord)
                {
                  set(linkTo14, bord, null);
                }
                                                                                                                                                                          
                public String getLabel14()
                {
                  return getString(Label14);
                }

                public void setLabel14(String s)
                {
                  setString(Label14, s, null);
                }
                
////////////////////////////////////////////////////////////////
//Label Command 15
////////////////////////////////////////////////////////////////
                                                                                                                                                            
                class LinkToCommand15
                extends Command

                {
                  public CommandArtifact doInvoke()
                  throws Exception
                {               
                  if(getShell() != null)
                {
                  BOrd bord = getLinkTo15();
                  if(!bord.isNull())
                  ((BIHyperlinkShell)getShell()).hyperlink(new HyperlinkInfo(bord));

                }
                  return null;
                }
                  BDropDownList thislabel15;
                  LinkToCommand15(BWidget bwidget)
                {
                  super(bwidget, getLabel15());
                }

                }
                                                                                                                                              
////////////////////////////////////////////////////////////////
//Label 15
////////////////////////////////////////////////////////////////

                public BOrd getLinkTo15()
                {
                  return (BOrd)get(linkTo15);
                }

                public void setLinkTo15(BOrd bord)
                {
                  set(linkTo15, bord, null);
                }
                                                                                                                                                                                        
                public String getLabel15()
                {
                  return getString(Label15);
                }

                public void setLabel15(String s)
                {
                  setString(Label15, s, null);
                }
                
////////////////////////////////////////////////////////////////
//Label Command 16
////////////////////////////////////////////////////////////////
                                                                                                                                                                          
                class LinkToCommand16
                extends Command

                {
                  public CommandArtifact doInvoke()
                  throws Exception
                {               
                  if(getShell() != null)
                {
                  BOrd bord = getLinkTo16();
                  if(!bord.isNull())
                  ((BIHyperlinkShell)getShell()).hyperlink(new HyperlinkInfo(bord));

                }
                  return null;
                }
                  BDropDownList thislabel16;
                  LinkToCommand16(BWidget bwidget)
                {
                  super(bwidget, getLabel16());
                }

                }
                                                                                                                                                            
////////////////////////////////////////////////////////////////
//Label 16
////////////////////////////////////////////////////////////////

                public BOrd getLinkTo16()
                {
                  return (BOrd)get(linkTo16);
                }

                public void setLinkTo16(BOrd bord)
                {
                  set(linkTo16, bord, null);
                }
                                                                                                                                                                                                      
                public String getLabel16()
                {
                  return getString(Label16);
                }

                public void setLabel16(String s)
                {
                  setString(Label16, s, null);
                }
                
////////////////////////////////////////////////////////////////
//Label Command 17
////////////////////////////////////////////////////////////////
                                                                                                                                                                                        
                class LinkToCommand17
                extends Command

                {
                  public CommandArtifact doInvoke()
                  throws Exception
                {               
                  if(getShell() != null)
                {
                  BOrd bord = getLinkTo17();
                  if(!bord.isNull())
                  ((BIHyperlinkShell)getShell()).hyperlink(new HyperlinkInfo(bord));

                }
                  return null;
                }
                  BDropDownList thislabel17;
                  LinkToCommand17(BWidget bwidget)
                {
                  super(bwidget, getLabel17());
                }

                }
                                                                                                                                                                          
////////////////////////////////////////////////////////////////
//Label 17
////////////////////////////////////////////////////////////////

                public BOrd getLinkTo17()
                {
                  return (BOrd)get(linkTo17);
                }

                public void setLinkTo17(BOrd bord)
                {
                  set(linkTo17, bord, null);
                }
                                                                                                                                                                                                                    
                public String getLabel17()
                {
                  return getString(Label17);
                }

                public void setLabel17(String s)
                {
                  setString(Label17, s, null);
                }
                
////////////////////////////////////////////////////////////////
//Label Command 18
////////////////////////////////////////////////////////////////
                                                                                                                                                                                                      
                class LinkToCommand18
                extends Command

                {
                  public CommandArtifact doInvoke()
                  throws Exception
                {               
                  if(getShell() != null)
                {
                  BOrd bord = getLinkTo18();
                  if(!bord.isNull())
                  ((BIHyperlinkShell)getShell()).hyperlink(new HyperlinkInfo(bord));

                }
                  return null;
                }
                  BDropDownList thislabel18;
                  LinkToCommand18(BWidget bwidget)
                {
                  super(bwidget, getLabel18());
                }

                }
                                                                                                                                                                                        
////////////////////////////////////////////////////////////////
//Label 18
////////////////////////////////////////////////////////////////

                public BOrd getLinkTo18()
                {
                  return (BOrd)get(linkTo18);
                }

                public void setLinkTo18(BOrd bord)
                {
                  set(linkTo18, bord, null);
                }
                                                                                                                                                                                                                                  
                public String getLabel18()
                {
                  return getString(Label18);
                }

                public void setLabel18(String s)
                {
                  setString(Label18, s, null);
                }
                
////////////////////////////////////////////////////////////////
//Label Command 19
////////////////////////////////////////////////////////////////
                                                                                                                                                                                                                    
                class LinkToCommand19
                extends Command

                {
                  public CommandArtifact doInvoke()
                  throws Exception
                {               
                  if(getShell() != null)
                {
                  BOrd bord = getLinkTo19();
                  if(!bord.isNull())
                  ((BIHyperlinkShell)getShell()).hyperlink(new HyperlinkInfo(bord));

                }
                  return null;
                }
                  BDropDownList thislabel19;
                  LinkToCommand19(BWidget bwidget)
                {
                  super(bwidget, getLabel19());
                }

                }
                                                                                                                                                                                                      
////////////////////////////////////////////////////////////////
//Label 19
////////////////////////////////////////////////////////////////

                public BOrd getLinkTo19()
                {
                  return (BOrd)get(linkTo19);
                }

                public void setLinkTo19(BOrd bord)
                {
                  set(linkTo19, bord, null);
                }
                                                                                                                                                                                                                                                
                public String getLabel19()
                {
                  return getString(Label19);
                }

                public void setLabel19(String s)
                {
                  setString(Label19, s, null);
                }
                
////////////////////////////////////////////////////////////////
//Label Command 20
////////////////////////////////////////////////////////////////
                                                                                                                                                                                                                                  
                class LinkToCommand20
                extends Command

                {
                  public CommandArtifact doInvoke()
                  throws Exception
                {               
                  if(getShell() != null)
                {
                  BOrd bord = getLinkTo20();
                  if(!bord.isNull())
                  ((BIHyperlinkShell)getShell()).hyperlink(new HyperlinkInfo(bord));

                }
                  return null;
                }
                  BDropDownList thislabel20;
                  LinkToCommand20(BWidget bwidget)
                {
                  super(bwidget, getLabel20());
                }

                }
                                                                                                                                                                                                                    
////////////////////////////////////////////////////////////////
//Label 20
////////////////////////////////////////////////////////////////

                public BOrd getLinkTo20()
                {
                  return (BOrd)get(linkTo20);
                }

                public void setLinkTo20(BOrd bord)
                {
                  set(linkTo20, bord, null);
                }
                                                                                                                                                                                                                                                              
                public String getLabel20()
                {
                  return getString(Label20);
                }

                public void setLabel20(String s)
                {
                  setString(Label20, s, null);
                }
                
////////////////////////////////////////////////////////////////
//Main
////////////////////////////////////////////////////////////////
                
                public String getText()
                {
                  return getString(text);
                }

                public void setText(String s)
                {
                  setString(text, s, null);
                }

                public void started()
                {
                }

                public void changed(Property property, Context context)
                {
                  isRunning();
                }

                public boolean isMouseOver()
                {
                  return mouseOver;
                }

                public boolean receiveInputEvents()
                {
                  return true;
                }

                public void mouseEntered(BMouseEvent bmouseevent)
                {
                  BWidgetShell bwidgetshell = getShell();
                  if(bwidgetshell != null)
                  {
                    savedShell = bwidgetshell;
                    bwidgetshell.showStatus(getText());
                    setMouseCursor(MouseCursor.hand);
                  }
                    mouseOver = true;
                }

                public void mouseExited(BMouseEvent bmouseevent)
                {
                  BWidgetShell bwidgetshell = getShell();
                  if(bwidgetshell != null)
                  bwidgetshell.showStatus(null);
                  mouseOver = false;
                  setMouseCursor(MouseCursor.normal);
                }

                public void mousePressed(BMouseEvent bmouseevent)
                {
                    BWidgetShell bwidgetshell = getShell();
                    super.mousePressed(bmouseevent);
                    if(bwidgetshell != null)
                    {
                      savedShell = bwidgetshell;
                      BMenu bmenu = makeLinktoMenu();
                      System.out.println("MousePressed popMenu = " + bmenu);
                      bmenu.open(bmouseevent);
                    }
                }
                
////////////////////////////////////////////////////////////////
//Make Menu
////////////////////////////////////////////////////////////////
                public BMenu makeLinktoMenu()
                {
                  linkToMenu.removeAll();
                  if(!getLinkTo01().isNull())
                    linkToMenu.add("Linkto01", new LinkToCommand01(this));
                  if(!getLinkTo02().isNull())
                    linkToMenu.add("Linkto02", new LinkToCommand02(this));
                  if(!getLinkTo03().isNull())
                    linkToMenu.add("LinkTo03", new LinkToCommand03(this));
                  if(!getLinkTo04().isNull())
                    linkToMenu.add("LinkTo04", new LinkToCommand04(this));
                  if(!getLinkTo05().isNull())
                    linkToMenu.add("LinkTo05", new LinkToCommand05(this));
                  if(!getLinkTo06().isNull())
                    linkToMenu.add("LinkTo06", new LinkToCommand06(this));
                  if(!getLinkTo07().isNull())
                    linkToMenu.add("LinkTo07", new LinkToCommand07(this));
                  if(!getLinkTo08().isNull())
                    linkToMenu.add("LinkTo08", new LinkToCommand08(this));
                  if(!getLinkTo09().isNull())
                    linkToMenu.add("LinkTo09", new LinkToCommand09(this));
                  if(!getLinkTo10().isNull())
                    linkToMenu.add("Linkto10", new LinkToCommand10(this));
                  if(!getLinkTo11().isNull())
                    linkToMenu.add("Linkto11", new LinkToCommand11(this));
                  if(!getLinkTo12().isNull())
                    linkToMenu.add("Linkto12", new LinkToCommand12(this));
                  if(!getLinkTo13().isNull())
                    linkToMenu.add("Linkto13", new LinkToCommand13(this));
                  if(!getLinkTo14().isNull())
                    linkToMenu.add("Linkto14", new LinkToCommand14(this));
                  if(!getLinkTo15().isNull())
                    linkToMenu.add("Linkto15", new LinkToCommand15(this));
                  if(!getLinkTo16().isNull())
                    linkToMenu.add("Linkto16", new LinkToCommand16(this));
                  if(!getLinkTo17().isNull())
                    linkToMenu.add("Linkto17", new LinkToCommand17(this));
                  if(!getLinkTo18().isNull())
                    linkToMenu.add("Linkto18", new LinkToCommand18(this));
                  if(!getLinkTo19().isNull())
                    linkToMenu.add("Linkto19", new LinkToCommand19(this));
                  if(!getLinkTo20().isNull())
                    linkToMenu.add("Linkto20", new LinkToCommand20(this));
                  return linkToMenu;
                }


                private final void thislabel()
                {
                  mouseOver = false;
                  savedShell = getShell();
                  linkToMenu = new BMenu();
                }

                public BDropDownList()
                {
                  thislabel();
                  setText(getText());
                  setImage(getImage());
                  setEnabled(getEnabled());
                  setLayout(BLayout.make(0.0D, 0, 0.0D, 0, 100D, 0, 25D, 0));
                }

////////////////////////////////////////////////////////////////
//Property 
////////////////////////////////////////////////////////////////
                                   
                 public static final Property text = newProperty(0, "MENU", null);
                 public static final Property linkTo01 = newProperty(8, BOrd.NULL, null);
                 public static final Property Label01 = newProperty(8, "Label 1", null);
                 public static final Property linkTo02 = newProperty(8, BOrd.NULL, null);
                 public static final Property Label02 = newProperty(8, "Label 2", null);
                 public static final Property linkTo03 = newProperty(8, BOrd.NULL, null);
                 public static final Property Label03 = newProperty(8, "Label 3", null);
                 public static final Property linkTo04 = newProperty(8, BOrd.NULL, null);
                 public static final Property Label04 = newProperty(8, "Label 4", null);
                 public static final Property linkTo05 = newProperty(8, BOrd.NULL, null);
                 public static final Property Label05 = newProperty(8, "Label 5", null);
                 public static final Property linkTo06 = newProperty(8, BOrd.NULL, null);
                 public static final Property Label06 = newProperty(8, "Label 6", null);
                 public static final Property linkTo07 = newProperty(8, BOrd.NULL, null);
                 public static final Property Label07 = newProperty(8, "Label 7", null);
                 public static final Property linkTo08 = newProperty(8, BOrd.NULL, null);
                 public static final Property Label08 = newProperty(8, "Label 8", null);
                 public static final Property linkTo09 = newProperty(8, BOrd.NULL, null);
                 public static final Property Label09 = newProperty(8, "Label 9", null);
                 public static final Property linkTo10 = newProperty(8, BOrd.NULL, null);
                 public static final Property Label10 = newProperty(8, "Label 10", null);
                 public static final Property linkTo11 = newProperty(8, BOrd.NULL, null);
                 public static final Property Label11 = newProperty(8, "Label 11", null);
                 public static final Property linkTo12 = newProperty(8, BOrd.NULL, null);
                 public static final Property Label12 = newProperty(8, "Label 12", null);
                 public static final Property linkTo13 = newProperty(8, BOrd.NULL, null);
                 public static final Property Label13 = newProperty(8, "Label 13", null);
                 public static final Property linkTo14 = newProperty(8, BOrd.NULL, null);
                 public static final Property Label14 = newProperty(8, "Label 14", null);
                 public static final Property linkTo15 = newProperty(8, BOrd.NULL, null);
                 public static final Property Label15 = newProperty(8, "Label 15", null);
                 public static final Property linkTo16 = newProperty(8, BOrd.NULL, null);
                 public static final Property Label16 = newProperty(8, "Label 16", null);
                 public static final Property linkTo17 = newProperty(8, BOrd.NULL, null);
                 public static final Property Label17 = newProperty(8, "Label 17", null);
                 public static final Property linkTo18 = newProperty(8, BOrd.NULL, null);
                 public static final Property Label18 = newProperty(8, "Label 18", null);
                 public static final Property linkTo19 = newProperty(8, BOrd.NULL, null);
                 public static final Property Label19 = newProperty(8, "Label 19", null);
                 public static final Property linkTo20 = newProperty(8, BOrd.NULL, null);
                 public static final Property Label20 = newProperty(8, "Label 20", null);
                 private boolean mouseOver;
                 BWidgetShell savedShell;
                 public BMenu linkToMenu;              

                
                
////////////////////////////////////////////////////////////////
//Final
////////////////////////////////////////////////////////////////
                                   
                public Type getType() { return TYPE; }
                public static final Type TYPE = Sys.loadType(BDropDownList.class);

                public BIcon getIcon()
                {
                  return icon;
                }

                private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/firefoxx.png");
          
}
