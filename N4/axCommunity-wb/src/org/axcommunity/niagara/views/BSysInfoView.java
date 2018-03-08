package org.axcommunity.niagara.views;

import javax.baja.converters.BINumericToNumber;
import javax.baja.converters.BObjectToString;
import javax.baja.gx.BBrush;
import javax.baja.gx.BColor;
import javax.baja.gx.BFont;
import javax.baja.gx.BSize;
import javax.baja.naming.BOrd;
import javax.baja.naming.SlotPath;
import javax.baja.sys.Action;
import javax.baja.sys.BComponent;
import javax.baja.sys.BComponentEvent;
import javax.baja.sys.BFacets;
import javax.baja.sys.BObject;
import javax.baja.sys.BString;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.ui.BBorder;
import javax.baja.ui.BLayout;
import javax.baja.ui.BValueBinding;
import javax.baja.ui.enums.BScaleMode;
import javax.baja.ui.enums.BValign;
import javax.baja.ui.pane.BCanvasPane;
import javax.baja.util.BFormat;
import javax.baja.workbench.view.BWbComponentView;

import org.axcommunity.niagara.system.BSysInfo;

import com.tridium.kitpx.BBargraph;
import com.tridium.kitpx.BBoundLabel;


public class BSysInfoView extends BWbComponentView
{
	/*------------------------------------------------------------------------------------------------------*/
	public static final Action updateState = newAction(Flags.HIDDEN, null);
	public void updateState(){invoke(updateState, null, null);}
	public void doUpdateState()
	{
		getContent().relayout();
		getContent().repaint();
	}
	
	/*------------------------------------------------------------------------------------------------------*/
	public BSysInfoView()
	{
		/*------------------------------------------------------------------------------------------------------*/
		/* CREATE THE BACKGROUND CANVAS THE OBJECTS WILL SHOW UP ON --------------------------------------------*/
		/*------------------------------------------------------------------------------------------------------*/
		canvas = new BCanvasPane();
		canvas.setViewSize(BSize.make(645,350));
		canvas.setScale(BScaleMode.none);

		/*------------------------------------------------------------------------------------------------------*/
		/* MAKE LABEL FOR BACKGROUND, NO BINDING ---------------------------------------------------------------*/
		/*------------------------------------------------------------------------------------------------------*/
		BBoundLabel lblBackground = new BBoundLabel();
		lblBackground.setLayout(BLayout.makeAbs(0,0,645,350));
		lblBackground.setBackground( BColor.gray.toBrush() );
		canvas.add(null,lblBackground);

		/*------------------------------------------------------------------------------------------------------*/
		/* MAKE LABEL FOR TITLE AT THE TOP, NO BINDING ---------------------------------------------------------*/
		/*------------------------------------------------------------------------------------------------------*/
		BBoundLabel lblTitle = new BBoundLabel();
		lblTitle.setLayout(BLayout.makeAbs(5,5,635,30));
		lblTitle.setBackground( BColor.dimGray.toBrush() );
		lblTitle.setForeground( BColor.white.toBrush() );
		lblTitle.setFont(BFont.make("Tahoma", 28, BFont.BOLD));
		lblTitle.setText("SYSTEM INFORMATION");
		canvas.add(null,lblTitle);
		
		
		/*------------------------------------------------------------------------------------------------------*/
		/* LABELS UNDER EACH OF THE BARGRAPHS ------------------------------------------------------------------*/
		/*------------------------------------------------------------------------------------------------------*/
		lblCpuUsage.setLayout(BLayout.makeAbs(x0,lblY,lblW,lblH));
		lblCpuUsage.setBackground(lblBack);
		lblCpuUsage.setForeground(lblFore); 
		lblCpuUsage.setFont(lblFont);
		lblCpuUsage.setBorder(lblBorder);
		lblCpuUsage.setText("Cpu\nUsage");
		lblCpuUsage.setValign(BValign.top);
		canvas.add(null, lblCpuUsage);
		
		lblOverallCpuUsage.setLayout(BLayout.makeAbs(x1,lblY,lblW,lblH));
		lblOverallCpuUsage.setBackground(lblBack);
		lblOverallCpuUsage.setForeground(lblFore); 
		lblOverallCpuUsage.setFont(lblFont);
		lblOverallCpuUsage.setBorder(lblBorder);
		lblOverallCpuUsage.setText("Overall\nCpu\nUsage");
		lblOverallCpuUsage.setValign(BValign.top);
		canvas.add(null, lblOverallCpuUsage);
		
		lblTotalPhysicalMemory.setLayout(BLayout.makeAbs(x2,lblY,lblW,lblH));
		lblTotalPhysicalMemory.setBackground(lblBack);
		lblTotalPhysicalMemory.setForeground(lblFore); 
		lblTotalPhysicalMemory.setFont(lblFont);
		lblTotalPhysicalMemory.setBorder(lblBorder);
		lblTotalPhysicalMemory.setText("Total\nPhysical\nMemory");
		lblTotalPhysicalMemory.setValign(BValign.top);
		canvas.add(null, lblTotalPhysicalMemory);
		
		lblFreePhysicalMemory.setLayout(BLayout.makeAbs(x3,lblY,lblW,lblH));
		lblFreePhysicalMemory.setBackground(lblBack);
		lblFreePhysicalMemory.setForeground(lblFore); 
		lblFreePhysicalMemory.setFont(lblFont);
		lblFreePhysicalMemory.setBorder(lblBorder);
		lblFreePhysicalMemory.setText("Free\nPhysical\nMemory");
		lblFreePhysicalMemory.setValign(BValign.top);
		canvas.add(null, lblFreePhysicalMemory);
		
		lblMaxHeap.setLayout(BLayout.makeAbs(x4,lblY,lblW,lblH));
		lblMaxHeap.setBackground(lblBack);
		lblMaxHeap.setForeground(lblFore); 
		lblMaxHeap.setFont(lblFont);
		lblMaxHeap.setBorder(lblBorder);
		lblMaxHeap.setText("Max\nHeap");
		lblMaxHeap.setValign(BValign.top);
		canvas.add(null, lblMaxHeap);
		
		lblTotalHeap.setLayout(BLayout.makeAbs(x5,lblY,lblW,lblH));
		lblTotalHeap.setBackground(lblBack);
		lblTotalHeap.setForeground(lblFore); 
		lblTotalHeap.setFont(lblFont);
		lblTotalHeap.setBorder(lblBorder);
		lblTotalHeap.setText("Total\nHeap");
		lblTotalHeap.setValign(BValign.top);
		canvas.add(null, lblTotalHeap);
		
		lblUsedHeap.setLayout(BLayout.makeAbs(x6,lblY,lblW,lblH));
		lblUsedHeap.setBackground(lblBack);
		lblUsedHeap.setForeground(lblFore); 
		lblUsedHeap.setFont(lblFont);
		lblUsedHeap.setBorder(lblBorder);
		lblUsedHeap.setText("Used\nHeap");
		lblUsedHeap.setValign(BValign.top);
		canvas.add(null, lblUsedHeap);
		
		lblFreeHeap.setLayout(BLayout.makeAbs(x7,lblY,lblW,lblH));
		lblFreeHeap.setBackground(lblBack);
		lblFreeHeap.setForeground(lblFore); 
		lblFreeHeap.setFont(lblFont);
		lblFreeHeap.setBorder(lblBorder);
		lblFreeHeap.setText("Free\nHeap");
		lblFreeHeap.setValign(BValign.top);
		canvas.add(null, lblFreeHeap);

		
		/*------------------------------------------------------------------------------------------------------*/
		/* SETUP THE BARGRAPHS ---------------------------------------------------------------------------------*/
		/*------------------------------------------------------------------------------------------------------*/
		barCountForCpuUsage.setLayout(BLayout.makeAbs(x0,barY,barW,barH));
		barCountForCpuUsage.setScaleVisible(false);
		barCountForCpuUsage.setFill(barFillStd);
		barCountForCpuUsage.setValueFont(barFont);
		barCountForCpuUsage.setBackground(barBack);
		barCountForCpuUsage.setForeground(barFore);
		
		barCountForOverallCpuUsage.setLayout(BLayout.makeAbs(x1,barY,barW,barH));
		barCountForOverallCpuUsage.setScaleVisible(false);
		barCountForOverallCpuUsage.setFill(barFillStd);
		barCountForOverallCpuUsage.setValueFont(barFont);
		barCountForOverallCpuUsage.setBackground(barBack);
		barCountForOverallCpuUsage.setForeground(barFore);
		
		barCountForTotalPhysicalMemory.setLayout(BLayout.makeAbs(x2,barY,barW,barH));
		barCountForTotalPhysicalMemory.setScaleVisible(false);
		barCountForTotalPhysicalMemory.setFill(barFillStd);
		barCountForTotalPhysicalMemory.setValueFont(barFont);
		barCountForTotalPhysicalMemory.setBackground(barBack);
		barCountForTotalPhysicalMemory.setForeground(barFore);
		
		barCountForFreePhysicalMemory.setLayout(BLayout.makeAbs(x3,barY,barW,barH));
		barCountForFreePhysicalMemory.setScaleVisible(false);
		barCountForFreePhysicalMemory.setFill(barFillStd);
		barCountForFreePhysicalMemory.setValueFont(barFont);
		barCountForFreePhysicalMemory.setBackground(barBack);
		barCountForFreePhysicalMemory.setForeground(barFore);
		
		barCountForMaxHeap.setLayout(BLayout.makeAbs(x4,barY,barW,barH));
		barCountForMaxHeap.setScaleVisible(false);
		barCountForMaxHeap.setFill(barFillStd);
		barCountForMaxHeap.setValueFont(barFont);
		barCountForMaxHeap.setBackground(barBack);
		barCountForMaxHeap.setForeground(barFore);
		
		barCountForTotalHeap.setLayout(BLayout.makeAbs(x5,barY,barW,barH));
		barCountForTotalHeap.setScaleVisible(false);
		barCountForTotalHeap.setFill(barFillStd);
		barCountForTotalHeap.setValueFont(barFont);
		barCountForTotalHeap.setBackground(barBack);
		barCountForTotalHeap.setForeground(barFore);
		
		barCountForUsedHeap.setLayout(BLayout.makeAbs(x6,barY,barW,barH));
		barCountForUsedHeap.setScaleVisible(false);
		barCountForUsedHeap.setFill(barFillStd);
		barCountForUsedHeap.setValueFont(barFont);
		barCountForUsedHeap.setBackground(barBack);
		barCountForUsedHeap.setForeground(barFore);
		
		barCountForFreeHeap.setLayout(BLayout.makeAbs(x7,barY,barW,barH));
		barCountForFreeHeap.setScaleVisible(false);
		barCountForFreeHeap.setFill(barFillStd);
		barCountForFreeHeap.setValueFont(barFont);
		barCountForFreeHeap.setBackground(barBack);
		barCountForFreeHeap.setForeground(barFore);
		
		//Set the content for the view...
		setContent(canvas);
	}


	/*------------------------------------------------------------------------------------------------------*/
	public void doLoadValue(BObject obj, Context cx)
	{
		BSysInfo		sysInfo		= (BSysInfo)obj;
		sysInfo.lease();
		SlotPath		basePath	= sysInfo.getSlotPath();

		canvas.add(null,barCountForCpuUsage);
		canvas.add(null,barCountForTotalPhysicalMemory);
		canvas.add(null,barCountForFreePhysicalMemory);
		canvas.add(null,barCountForOverallCpuUsage);
		canvas.add(null,barCountForUsedHeap);
		canvas.add(null,barCountForMaxHeap);
		canvas.add(null,barCountForTotalHeap);
		canvas.add(null,barCountForFreeHeap);
		
		/**
		 * If you use the binding method, you won't necessarily need
		 * to call the 'updateValues' method since it should update the
		 * values automatically.
		 * 
		 * So in lieu of using binding we call the 'updateValues' method
		 * to populate the initial values upon loading of this view.
		 * Otherwise, the values won't update until a value changes
		 * on the SysInfo object which would trigger the 'handleComponentEvent'
		 * method thus updating the values.
		 */
		updateValues(sysInfo);
		
		
		
		/*
		 * If you wanted to use binding to populate the values 
		 * you could use the following snippet of code.
		 * 
		 * However, instead of binding I'm just using the 
		 * 'handleComponentEvent' method to update the values
		 * when they change.
		
		BFacets	facetsA	= BFacets.makeNumeric(0);
		BFacets	facetsB	= BFacets.make("showSeparators", true);
		BFacets	facetsC	= BFacets.make(facetsA,facetsB);
	
		//-----------------------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------------------
		//--  VALUE BINDING ----------------
		BValueBinding		cpuUsageBindVal		= new BValueBinding();
		SlotPath			cpuUsageBindValPath	= basePath.merge(new SlotPath("cpuUsage/value"));
		BOrd				cpuUsageBindValOrd	= BOrd.make(sysInfo.getHandleOrd(),cpuUsageBindValPath);
		cpuUsageBindVal.setPopupEnabled(false);
		cpuUsageBindVal.setOrd(cpuUsageBindValOrd);
		BINumericToNumber	cpuUsageValCvt		= new BINumericToNumber();
		cpuUsageBindVal.add("value",  cpuUsageValCvt, 0, facetsC, null);
		barCountForCpuUsage.add(null,	cpuUsageBindVal);
		//--  TEXT BINDING ---------------- 
		BValueBinding		cpuUsageBindTxt		= new BValueBinding();
		SlotPath			cpuUsageBindTxtPath	= basePath.merge(new SlotPath("cpuUsage/value"));
		BOrd				cpuUsageBindTxtOrd	= BOrd.make(sysInfo.getHandleOrd(),cpuUsageBindTxtPath);
		cpuUsageBindTxt.setPopupEnabled(false);
		cpuUsageBindTxt.setOrd(cpuUsageBindTxtOrd);
		BObjectToString cpuUsageTxtCvt = new BObjectToString();
		cpuUsageTxtCvt.setFormat(BFormat.make("%.%"));
		cpuUsageBindTxt.add("text",  cpuUsageTxtCvt, 0, facetsC, null);
		barCountForCpuUsage.add(null,	cpuUsageBindTxt);
		//--  MAX VALUE BINDING ---------------- 
		BValueBinding		cpuUsageBind			= new BValueBinding();
		SlotPath			cpuUsageBindPath		= basePath.merge(new SlotPath("cpuUsage/value"));
		BOrd				cpuUsageBindOrd			= BOrd.make(sysInfo.getHandleOrd(),cpuUsageBindPath);
		cpuUsageBind.setPopupEnabled(false);
		cpuUsageBind.setOrd(cpuUsageBindOrd);
		BINumericToNumber	cpuUsageCvt				= new BINumericToNumber();
		cpuUsageBind.add("max",			cpuUsageCvt);
		barCountForCpuUsage.add(null,	cpuUsageBind);
		canvas.add(null,barCountForCpuUsage);		
		*/
		
		
		canvas.repaint();
		updateState();
		
		
	}

	/*------------------------------------------------------------------------------------------------------*/
	/**
	  * This is called from the BWbComponentView route method.
	  */
	public void handleComponentEvent(BComponentEvent event)
	{
		//Check for the property changed event
		if( event.getId() == BComponentEvent.PROPERTY_CHANGED)
		{
			//The slot that triggered this event, but not utilized in this logic.
			//String			slotName		= event.getSlotName();
			
			BComponent		source			= event.getSourceComponent();
			BSysInfo		info			= (BSysInfo) source;
			
			updateValues(info);
			
		}
	}
	
	/*------------------------------------------------------------------------------------------------------*/
	/**
	 * This is where the bar graph values are updated.
	 * @param info BSysInfo
	 */
	public void updateValues(BSysInfo info)
	{
		/*-- CpuUsage ----------------------------------------------------------------------------------------------------------*/
		barCountForCpuUsage.setValue(info.getCpuUsage().getValue());
		barCountForCpuUsage.setMax( 100 );
		barCountForCpuUsage.setText( String.valueOf((int) info.getCpuUsage().getValue()) + " %");
		if(info.getCpuUsage().getValue()	>= 25){barCountForCpuUsage.setFill(barFillRed);}
		else{barCountForCpuUsage.setFill(barFillGreen);}
		
		/*-- OverallCpuUsage ---------------------------------------------------------------------------------------------------*/
		barCountForOverallCpuUsage.setValue(info.getOverallCpuUsage().getValue());
		barCountForOverallCpuUsage.setMax( 100 );
		barCountForOverallCpuUsage.setText( String.valueOf((int) info.getOverallCpuUsage().getValue()) + " %");
		if(info.getOverallCpuUsage().getValue()	> 5){barCountForOverallCpuUsage.setFill(barFillRed);}
		else{barCountForOverallCpuUsage.setFill(barFillGreen);}
		
		/*-- TotalPhysicalMemory -----------------------------------------------------------------------------------------------*/
		barCountForTotalPhysicalMemory.setValue(info.getTotalPhysicalMemory().getValue()/1024.0);
		barCountForTotalPhysicalMemory.setMax( info.getTotalPhysicalMemory().getValue()/1024.0 );
		barCountForTotalPhysicalMemory.setText( String.valueOf((int) (info.getTotalPhysicalMemory().getValue()/1024.0)) + " MB");
		if( (info.getTotalPhysicalMemory().getValue()/1024) >= 16000 ){barCountForTotalPhysicalMemory.setFill(barFillGreen);}
		else if( (info.getTotalPhysicalMemory().getValue()/1024) >= 8000 ){barCountForTotalPhysicalMemory.setFill(barFillYellow);}
		else{barCountForTotalPhysicalMemory.setFill(barFillRed);}
		
		/*-- FreePhysicalMemory ------------------------------------------------------------------------------------------------*/
		barCountForFreePhysicalMemory.setValue(info.getFreePhysicalMemory().getValue()/1024.0);
		barCountForFreePhysicalMemory.setMax( info.getTotalPhysicalMemory().getValue()/1024.0 );
		barCountForFreePhysicalMemory.setText( String.valueOf((int) (info.getFreePhysicalMemory().getValue()/1024.0)) + " MB");
		if( (info.getFreePhysicalMemory().getValue()/1024) / barCountForFreePhysicalMemory.getMax() < .5 ){barCountForFreePhysicalMemory.setFill(barFillRed);}
		else if(info.getFreePhysicalMemory().getValue() / barCountForFreePhysicalMemory.getMax() < .75 ){barCountForFreePhysicalMemory.setFill(barFillYellow);}
		else{barCountForFreePhysicalMemory.setFill(barFillGreen);}
		
		/*-- MaxHeap -----------------------------------------------------------------------------------------------------------*/
		barCountForMaxHeap.setValue(info.getMaxHeap().getValue()/1024.0);
		barCountForMaxHeap.setMax( info.getTotalPhysicalMemory().getValue()/1024 );
		barCountForMaxHeap.setText( String.valueOf((int) (info.getMaxHeap().getValue()/1024.0)) + " MB");
		if( (info.getMaxHeap().getValue()/1024) / barCountForMaxHeap.getMax() < .75 ){barCountForMaxHeap.setFill(barFillGreen);}
		else{barCountForMaxHeap.setFill(barFillRed);}
		
		/*-- TotalHeap ---------------------------------------------------------------------------------------------------------*/
		barCountForTotalHeap.setValue(info.getTotalHeap().getValue()/1024.0);
		barCountForTotalHeap.setMax(info.getMaxHeap().getValue()/1024.0);
		barCountForTotalHeap.setText( String.valueOf((int) (info.getTotalHeap().getValue()/1024.0)) + " MB");
		if(info.getTotalHeap().getValue() / info.getMaxHeap().getValue() < .75 ){barCountForTotalHeap.setFill(barFillGreen);}
		else{barCountForTotalHeap.setFill(barFillRed);}
		
		/*-- UsedHeap ----------------------------------------------------------------------------------------------------------*/
		barCountForUsedHeap.setValue(info.getUsedHeap().getValue()/1024.0);
		barCountForUsedHeap.setMax(info.getMaxHeap().getValue()/1024.0);
		barCountForUsedHeap.setText( String.valueOf((int) (info.getUsedHeap().getValue()/1024.0)) + " MB");
		if(info.getUsedHeap().getValue()	>= info.getFreeHeap().getValue()){barCountForUsedHeap.setFill(barFillRed);}
		else{barCountForUsedHeap.setFill(barFillGreen);}
		
		/*-- FreeHeap ----------------------------------------------------------------------------------------------------------*/
		barCountForFreeHeap.setValue(info.getFreeHeap().getValue()/1024.0);
		barCountForFreeHeap.setMax(info.getTotalHeap().getValue()/1024.0);
		barCountForFreeHeap.setText( String.valueOf((int) (info.getFreeHeap().getValue()/1024.0)) + " MB");
		if(info.getFreeHeap().getValue()	>= info.getUsedHeap().getValue()){barCountForFreeHeap.setFill(barFillGreen);}
		else{barCountForFreeHeap.setFill(barFillRed);}
	}

	
	
	private	BCanvasPane		canvas;
	
	private	BBoundLabel		lblCpuUsage						= new BBoundLabel();
	private	BBoundLabel		lblTotalPhysicalMemory			= new BBoundLabel();
	private	BBoundLabel		lblFreePhysicalMemory			= new BBoundLabel();
	private	BBoundLabel		lblOverallCpuUsage				= new BBoundLabel();
	private	BBoundLabel		lblUsedHeap						= new BBoundLabel();
	private	BBoundLabel		lblMaxHeap						= new BBoundLabel();
	private	BBoundLabel		lblTotalHeap					= new BBoundLabel();
	private	BBoundLabel		lblFreeHeap						= new BBoundLabel();

	private	BBargraph		barCountForCpuUsage				= new BBargraph();
	private	BBargraph		barCountForTotalPhysicalMemory	= new BBargraph();
	private	BBargraph		barCountForFreePhysicalMemory	= new BBargraph();
	private	BBargraph		barCountForOverallCpuUsage		= new BBargraph();
	private	BBargraph		barCountForUsedHeap				= new BBargraph();
	private	BBargraph		barCountForMaxHeap				= new BBargraph();
	private	BBargraph		barCountForTotalHeap			= new BBargraph();
	private	BBargraph		barCountForFreeHeap				= new BBargraph();
	
	private	Double			BarXoffset			= 80D;
	private	Double			x0					= 5D;
	private	Double			x1					= BarXoffset * 1 + x0;
	private	Double			x2					= BarXoffset * 2 + x0;
	private	Double			x3					= BarXoffset * 3 + x0;
	private	Double			x4					= BarXoffset * 4 + x0;
	private	Double			x5					= BarXoffset * 5 + x0;
	private	Double			x6					= BarXoffset * 6 + x0;
	private	Double			x7					= BarXoffset * 7 + x0;
		
	private	Double			barY				= 40D;
	private	Double			lblY				= 265D;
		
	private	Double			barW				= 75D;
	private	Double			barH				= 220D;
		
	private	Double			lblW				= barW;
	private	Double			lblH				= 50D;
	
	private	BFont			barFont				= BFont.make("Tahoma", 14, BFont.BOLD);
	private	BBrush			barFore				= BBrush.makeSolid(BColor.make(0,0,0,255));				//#ff000000, black
	private	BBrush			barBack				= BBrush.makeSolid(BColor.make(192,192,192,255));		//#ffc0c0c0, silver
	private	BBrush			barFillStd			= BColor.make(65, 105, 225, 255).toBrush();				//#ff4169e1, royalBlue
	private	BBrush			barFillRed 			= BColor.darkRed.toBrush();
	private	BBrush			barFillYellow		= BColor.goldenrod.toBrush();
	private	BBrush			barFillGreen		= BColor.green.toBrush();
	
	private	BBrush			lblBack				= BColor.transparent.toBrush();
	private	BBrush			lblFore				= BColor.black.toBrush();
	private	BFont			lblFont				= BFont.make("Tahoma", 12, BFont.BOLD);
	private	BBorder 		lblBorder 			= BBorder.make(0, BBorder.NONE, BColor.black.toBrush());

  
	public Type getType() { return TYPE; }
	public static final Type TYPE = Sys.loadType(BSysInfoView.class);
  
}
