package me.lor3mipsum.next.client.core.gui.themes;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.theme.*;
import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.client.impl.modules.client.ClickGuiModule;

import java.awt.*;

public class NextTheme extends ThemeBase {
    protected int height,padding,scroll;
    protected String separator;

    private ClickGuiModule clickGuiModule = Main.moduleManager.getModule(ClickGuiModule.class);

    public NextTheme (IColorScheme scheme, int height, int padding, int scroll, String separator) {
        super(scheme);
        this.height=height;
        this.padding=padding;
        this.scroll=scroll;
        this.separator=separator;
        scheme.createSetting(this,"Main Color","The color for panel outlines.",false,true,new Color(255,0,0),false);
        scheme.createSetting(this,"Enabled Color","The main color for enabled components.",true,true,new Color(255,0,0,150),false);
        scheme.createSetting(this,"Disabled Color","The main color for disabled modules.",false,true,new Color(0,0,0),false);
        scheme.createSetting(this,"Settings Color","The background color for settings.",false,true,new Color(30,30,30),false);
        scheme.createSetting(this,"Font Color","The main color for text.",false,true,new Color(255,255,255),false);
        scheme.createSetting(this,"Highlight Color","The color for highlighted text.",false,true,new Color(0,0,255),false);
    }

    protected void fillBaseRect (Context context, boolean focus, boolean active, int logicalLevel, int graphicalLevel, Color colorState) {
        Color color=getMainColor(focus,active);
        if (logicalLevel>1 && !active) color=getBackgroundColor(focus);
        else if (graphicalLevel<=0 && active) color= scheme.getColor("Settings Color");
        if (colorState!=null) color=colorState;
        context.getInterface().fillRect(context.getRect(),color,color,color,color);
    }

    protected void renderOverlay (Context context) {
        Color color=context.isHovered()?new Color(255,255,255,64):new Color(0,0,0,0);
        context.getInterface().fillRect(context.getRect(),color,color,color,color);
    }

    @Override
    public IDescriptionRenderer getDescriptionRenderer() {
        return (inter, pos, text) -> {
            if(clickGuiModule.descriptionMode.getValue() == ClickGuiModule.DescriptionMode.Mouse) {
                Point mouseDescPos=pos;
                Rectangle r=new Rectangle(mouseDescPos,new Dimension(inter.getFontWidth(height, text)+2, height+2));
                Color bgcolor=scheme.getColor("Settings Color");
                inter.fillRect(r,bgcolor,bgcolor,bgcolor,bgcolor);
                Color color = scheme.getColor("Main Color");
                inter.drawRect(r,color,color,color,color);
                Color textColor = getFontColor(true);
                inter.drawString(new Point(mouseDescPos.x+1,mouseDescPos.y+1), height, text,textColor);
            } else {
                Point fixedDescPos= new Point(0, 0);
                Rectangle r=new Rectangle(fixedDescPos,new Dimension(inter.getFontWidth(height, text)+2, height+2));
                Color bgcolor=scheme.getColor("Settings Color");
                inter.fillRect(r,bgcolor,bgcolor,bgcolor,bgcolor);
                Color color = scheme.getColor("Main Color");
                inter.drawRect(r,color,color,color,color);
                Color textColor = getFontColor(true);
                inter.drawString(new Point(fixedDescPos.x+1,fixedDescPos.y+1), height, text,textColor);
            }
        };
    }

    @Override
    public IContainerRenderer getContainerRenderer (int logicalLevel, int graphicalLevel, boolean horizontal) {
        return new IContainerRenderer() {

            @Override
            public void renderBackground (Context context, boolean focus) {
                if (graphicalLevel>0 && (graphicalLevel != 1 && clickGuiModule.csgoLayout.getValue())) {
                    Color color=getBackgroundColor(focus);
                    context.getInterface().fillRect(context.getRect(),color,color,color,color);
                }
                if (graphicalLevel == 1 && clickGuiModule.csgoLayout.getValue()) {
                    Color color=getMainColor(focus, false);
                    context.getInterface().fillRect(context.getRect(),color,color,color,color);
                }
            }

            @Override
            public int getTop() {
                return graphicalLevel<=0?0:0;
            }

            @Override
            public int getBottom() {
                return graphicalLevel<=0?0:0;
            }
        };
    }

    @Override
    public <T> IPanelRenderer<T> getPanelRenderer (Class<T> type, int logicalLevel, int graphicalLevel) {
        return new IPanelRenderer<T>() {
            @Override
            public void renderPanelOverlay(Context context, boolean focus, T state, boolean open) {
                if (graphicalLevel==0 && !open) {
                    context.getInterface().fillRect(new Rectangle(context.getPos().x + context.getSize().width - 12, context.getPos().y + context.getSize().height - 9, 8, 2), getFontColor(focus), getFontColor(focus), getFontColor(focus), getFontColor(focus));
                }

                if (graphicalLevel==0 && open) {
                    Color color= scheme.getColor("Main Color");
                    context.getInterface().fillRect(new Rectangle(context.getPos().x,context.getPos().y+context.getSize().height,context.getSize().width,3),color,color,color,color);
                    context.getInterface().fillRect(new Rectangle(context.getPos().x + context.getSize().width - 12, context.getPos().y + 6, 8, 2), getFontColor(focus), getFontColor(focus), getFontColor(focus), getFontColor(focus));
                    context.getInterface().fillRect(new Rectangle(context.getPos().x + context.getSize().width - 9, context.getPos().y + 3, 2, 8), getFontColor(focus), getFontColor(focus), getFontColor(focus), getFontColor(focus));
                }
            }

            @Override
            public void renderTitleOverlay(Context context, boolean focus, T state, boolean open) {
                if (graphicalLevel==0 && open) {
                    Color color= scheme.getColor("Main Color");
                    context.setHeight(context.getSize().height + 3);
                    context.getInterface().fillRect(new Rectangle(context.getPos().x,context.getPos().y+context.getSize().height-3,context.getSize().width,3),color,color,color,color);
                }
            }
        };
    }

    @Override
    public <T> IScrollBarRenderer<T> getScrollBarRenderer (Class<T> type, int logicalLevel, int graphicalLevel) {
        return new IScrollBarRenderer<T>() {
            @Override
            public int renderScrollBar(Context context, boolean focus, T state, boolean horizontal, int height, int position) {
                return position;
            }

            @Override
            public int getThickness() {
                return 0;
            }
        };
    }

    @Override
    public <T> IEmptySpaceRenderer<T> getEmptySpaceRenderer (Class<T> type, int logicalLevel, int graphicalLevel, boolean container) {
        return (context,focus,state)->{
            if (graphicalLevel==0) {
                Color color=getBackgroundColor(focus);
                context.getInterface().fillRect(context.getRect(),color,color,color,color);
            }
        };
    }

    @Override
    public <T> IButtonRenderer<T> getButtonRenderer (Class<T> type, int logicalLevel, int graphicalLevel, boolean container) {
        return new IButtonRenderer<T>() {
            @Override
            public void renderButton(Context context, String title, boolean focus, T state) {
                if (type==Boolean.class)
                    context.getInterface().fillRect(context.getRect(),getMainColor(focus, (Boolean) state), getMainColor(focus, (Boolean) state), getMainColor(focus, (Boolean) state), getMainColor(focus, (Boolean) state));
                else if (type==Color.class) {
                    context.getInterface().fillRect(context.getRect(),getBackgroundColor(focus), getBackgroundColor(focus), getBackgroundColor(focus), getBackgroundColor(focus));
                    context.getInterface().fillRect(new Rectangle(context.getPos().x + context.getSize().width - 12, context.getPos().y + 3, 9, 9), (Color)state, (Color)state, (Color)state, (Color)state);
                }
                else if (graphicalLevel == 0)
                    context.getInterface().fillRect(context.getRect(),getMainColor(focus, false), getMainColor(focus, false), getMainColor(focus, false), getMainColor(focus, false));
                else
                    context.getInterface().fillRect(context.getRect(),getBackgroundColor(focus), getBackgroundColor(focus), getBackgroundColor(focus), getBackgroundColor(focus));

                renderOverlay(context);
                if (type==String.class) context.getInterface().drawString(new Point(context.getRect().x+padding,context.getRect().y+padding),height,title+separator+state,getFontColor(focus));
                else if (graphicalLevel == 0) context.getInterface().drawString(new Point(context.getPos().x+context.getSize().width/2 - context.getInterface().getFontWidth(0, title)/2,context.getRect().y+padding),height,title,getFontColor(focus));
                else context.getInterface().drawString(new Point(context.getRect().x+padding,context.getRect().y+padding),height,title,getFontColor(focus));
            }

            @Override
            public int getDefaultHeight() {
                return getBaseHeight();
            }
        };
    }

    @Override
    public IButtonRenderer<Void> getSmallButtonRenderer (int symbol, int logicalLevel, int graphicalLevel, boolean container) {
        return new IButtonRenderer<Void>() {
            @Override
            public void renderButton (Context context, String title, boolean focus, Void state) {
                //fillBaseRect(context,focus,true,logicalLevel,graphicalLevel,null);
                renderOverlay(context);
                Point points[]=new Point[3];
                Rectangle rect=new Rectangle(context.getRect().x+padding,context.getRect().y+padding,context.getRect().height-2*padding,context.getRect().height-2*padding);
                if (title==null) rect.x+=context.getRect().width/2-context.getRect().height/2;
                switch (symbol) {
                    case ITheme.CLOSE:
                        break;
                    case ITheme.MINIMIZE:
                        break;
                    case ITheme.ADD:
                        break;
                    case ITheme.LEFT:
                        if (rect.height%2==1) rect.height-=1;
                        points[0]=new Point(rect.x,rect.y);
                        points[1]=new Point(rect.x,rect.y+rect.height);
                        points[2]=new Point(rect.x+rect.width,rect.y+rect.height/2);
                        break;
                    case ITheme.RIGHT:
                        if (rect.height%2==1) rect.height-=1;
                        points[2]=new Point(rect.x+rect.width,rect.y);
                        points[1]=new Point(rect.x+rect.width,rect.y+rect.height);
                        points[0]=new Point(rect.x,rect.y+rect.height/2);
                        break;
                    case ITheme.UP:
                        if (rect.width%2==1) rect.width-=1;
                        points[0]=new Point(rect.x,rect.y+rect.height);
                        points[1]=new Point(rect.x+rect.width,rect.y+rect.height);
                        points[2]=new Point(rect.x+rect.width/2,rect.y);
                        break;
                    case ITheme.DOWN:
                        if (rect.width%2==1) rect.width-=1;
                        points[0]=new Point(rect.x,rect.y);
                        points[1]=new Point(rect.x+rect.width,rect.y);
                        points[2]=new Point(rect.x+rect.width/2,rect.y+rect.height);
                        break;
                }
                if ((symbol>=ITheme.LEFT && symbol<=ITheme.DOWN)) {
                    Color color=getFontColor(focus);
                    context.getInterface().fillTriangle(points[0],points[1],points[2],color,color,color);
                }
                if (title!=null) context.getInterface().drawString(new Point(context.getRect().x+(symbol==ITheme.NONE?padding:context.getRect().height),context.getRect().y+padding),height,title,getFontColor(focus));
            }

            @Override
            public int getDefaultHeight() {
                return getBaseHeight();
            }
        };
    }

    @Override
    public IButtonRenderer<String> getKeybindRenderer(int logicalLevel, int graphicalLevel, boolean container) {
        return new IButtonRenderer<String>() {
            @Override
            public void renderButton(Context context, String title, boolean focus, String state) {
                fillBaseRect(context,focus,focus,logicalLevel,graphicalLevel,null);
                renderOverlay(context);
                context.getInterface().drawString(new Point(context.getRect().x+padding,context.getRect().y+padding),height,title+separator+(focus?"...":state),getFontColor(focus));
            }

            @Override
            public int getDefaultHeight() {
                return getBaseHeight();
            }
        };
    }

    @Override
    public ISliderRenderer getSliderRenderer(int logicalLevel, int graphicalLevel, boolean container) {
        return new ISliderRenderer() {
            @Override
            public void renderSlider(Context context, String title, String state, boolean focus, double value) {
                Color colorA=getMainColor(focus,true),colorB=getBackgroundColor(focus);
                Rectangle rect=getSlideArea(context);
                int divider=(int)(rect.width*value);
                context.getInterface().fillRect(new Rectangle(rect.x,rect.y,divider,rect.height),colorA,colorA,colorA,colorA);
                context.getInterface().fillRect(new Rectangle(rect.x+divider,rect.y,rect.width-divider,rect.height),colorB,colorB,colorB,colorB);
                renderOverlay(context);
                context.getInterface().drawString(new Point(context.getRect().x+padding,context.getRect().y+padding),height,title+separator+state,getFontColor(focus));
            }

            @Override
            public int getDefaultHeight() {
                return getBaseHeight();
            }
        };
    }

    @Override
    public IRadioRenderer getRadioRenderer(int logicalLevel, int graphicalLevel, boolean container) {
        return new IRadioRenderer() {
            @Override
            public void renderItem (Context context, ILabeled[] items, boolean focus, int target, double state, boolean horizontal) {
                for (int i=0;i<items.length;i++) {
                    Rectangle rect=getItemRect(context,items,i,horizontal);
                    Context subContext=new Context(context.getInterface(),rect.width,rect.getLocation(),context.hasFocus(),context.onTop());
                    subContext.setHeight(rect.height);
                    fillBaseRect(subContext,focus,i==target,logicalLevel,graphicalLevel,null);
                    renderOverlay(subContext);
                    context.getInterface().drawString(new Point(rect.x+padding,rect.y+padding),height,items[i].getDisplayName(),getFontColor(focus));
                }
            }

            @Override
            public int getDefaultHeight (ILabeled[] items, boolean horizontal) {
                return (horizontal?1:items.length)*getBaseHeight();
            }
        };
    }

    @Override
    public IResizeBorderRenderer getResizeRenderer() {
        return new IResizeBorderRenderer() {
            @Override
            public void drawBorder(Context context, boolean focus) {
                Color color=getBackgroundColor(focus);
                Rectangle rect=context.getRect();
                context.getInterface().fillRect(new Rectangle(rect.x,rect.y,rect.width,getBorder()),color,color,color,color);
                context.getInterface().fillRect(new Rectangle(rect.x,rect.y+rect.height-getBorder(),rect.width,getBorder()),color,color,color,color);
                context.getInterface().fillRect(new Rectangle(rect.x,rect.y+getBorder(),getBorder(),rect.height-2*getBorder()),color,color,color,color);
                context.getInterface().fillRect(new Rectangle(rect.x+rect.width-getBorder(),rect.y+getBorder(),getBorder(),rect.height-2*getBorder()),color,color,color,color);
            }

            @Override
            public int getBorder() {
                return 2;
            }
        };
    }

    @Override
    public ITextFieldRenderer getTextRenderer (int logicalLevel, int graphicalLevel, boolean container) {
        return new ITextFieldRenderer() {
            @Override
            public int renderTextField (Context context, String title, boolean focus, String content, int position, int select, int boxPosition, boolean insertMode) {
                // Declare and assign variables
                Color color=focus?scheme.getColor("Main Color"):scheme.getColor("Settings Color");
                Color textColor=getFontColor(focus);
                Color highlightColor=scheme.getColor("Highlight Color");
                Rectangle rect=getTextArea(context,content);
                int strlen=context.getInterface().getFontWidth(height,content.substring(0,position));
                // Deal with box render offset
                if (boxPosition<position) {
                    int minPosition=boxPosition;
                    while (minPosition<position) {
                        if (context.getInterface().getFontWidth(height,content.substring(0,minPosition))+rect.width-padding>=strlen) break;
                        minPosition++;
                    }
                    if (boxPosition<minPosition) boxPosition=minPosition;
                } else if (boxPosition>position) boxPosition=position-1;
                int maxPosition=content.length();
                while (maxPosition>0) {
                    if (context.getInterface().getFontWidth(height,content.substring(maxPosition))>=rect.width-padding) {
                        maxPosition++;
                        break;
                    }
                    maxPosition--;
                }
                if (boxPosition>maxPosition) boxPosition=maxPosition;
                else if (boxPosition<0) boxPosition=0;
                int offset=context.getInterface().getFontWidth(height,content.substring(0,boxPosition));
                // Deal with highlighted text
                int x1=rect.x+padding/2-offset+strlen;
                int x2=rect.x+padding/2-offset;
                if (position<content.length()) x2+=context.getInterface().getFontWidth(height,content.substring(0,position+1));
                else x2+=context.getInterface().getFontWidth(height,content+"X");
                // Draw stuff around the box
                fillBaseRect(context,focus,false,logicalLevel,graphicalLevel,null);
                renderOverlay(context);
                if (title!=null) context.getInterface().drawString(new Point(context.getRect().x+padding,context.getRect().y+padding),height,title,textColor);
                // Draw the box
                context.getInterface().window(rect);
                if (select>=0) {
                    int x3=rect.x+padding/2-offset+context.getInterface().getFontWidth(height,content.substring(0,select));
                    context.getInterface().fillRect(new Rectangle(Math.min(x1,x3),rect.y+padding/2,Math.abs(x3-x1),height),highlightColor,highlightColor,highlightColor,highlightColor);
                }
                context.getInterface().drawString(new Point(rect.x+padding/2-offset,rect.y+padding/2),height,content,textColor);
                if ((System.currentTimeMillis()/500)%2==0) {
                    if (insertMode) context.getInterface().fillRect(new Rectangle(x1,rect.y+padding/2+height,x2-x1,1),textColor,textColor,textColor,textColor);
                    else context.getInterface().fillRect(new Rectangle(x1,rect.y+padding/2,1,height),textColor,textColor,textColor,textColor);
                }
                context.getInterface().fillRect(new Rectangle(rect.x,rect.y,rect.width,1),color,color,color,color);
                context.getInterface().fillRect(new Rectangle(rect.x,rect.y+rect.height-1,rect.width,1),color,color,color,color);
                context.getInterface().fillRect(new Rectangle(rect.x,rect.y,1,rect.height),color,color,color,color);
                context.getInterface().fillRect(new Rectangle(rect.x+rect.width-1,rect.y,1,rect.height),color,color,color,color);
                context.getInterface().restore();
                return boxPosition;
            }

            @Override
            public int getDefaultHeight() {
                return 2*getBaseHeight();
            }

            @Override
            public Rectangle getTextArea (Context context, String title) {
                Rectangle rect=context.getRect();
                return title==null?rect:new Rectangle(rect.x+padding,rect.y+getBaseHeight(),rect.width-2*padding,rect.height-getBaseHeight()-padding);
            }

            @Override
            public int transformToCharPos(Context context, String content, int boxPosition) {
                Rectangle rect=getTextArea(context,content);
                Point mouse=context.getInterface().getMouse();
                int offset=context.getInterface().getFontWidth(height,content.substring(0,boxPosition));
                if (rect.contains(mouse)) {
                    for (int i=1;i<=content.length();i++) {
                        if (rect.x+padding/2-offset+context.getInterface().getFontWidth(height,content.substring(0,i))>mouse.x) {
                            return i-1;
                        }
                    }
                    return content.length();
                }
                return -1;
            }
        };
    }

    public ISwitchRenderer<Boolean> getToggleSwitchRenderer (int logicalLevel, int graphicalLevel, boolean container) {
        return new ISwitchRenderer<Boolean>() {
            @Override
            public void renderButton(Context context, String title, boolean focus, Boolean state) {
                fillBaseRect(context,focus,false,logicalLevel,graphicalLevel,null);
                context.getInterface().fillRect(context.getRect(), getBackgroundColor(focus),  getBackgroundColor(focus), getBackgroundColor(focus), getBackgroundColor(focus));
                renderOverlay(context);
                context.getInterface().drawString(new Point(context.getRect().x+padding,context.getRect().y+padding),height,title+separator+(state?"On":"Off"),getFontColor(focus));

                context.getInterface().fillRect(new Rectangle(getOffField(context).x, getOffField(context).y, 1, getOffField(context).height), scheme.getColor("Main Color"), scheme.getColor("Main Color"), scheme.getColor("Main Color"), scheme.getColor("Main Color"));
                context.getInterface().fillRect(new Rectangle(getOffField(context).x, getOffField(context).y, getOffField(context).width, 1), scheme.getColor("Main Color"), scheme.getColor("Main Color"), scheme.getColor("Main Color"), scheme.getColor("Main Color"));
                context.getInterface().fillRect(new Rectangle(getOffField(context).x + getOffField(context).width - 1, getOffField(context).y, 1, getOffField(context).height), scheme.getColor("Main Color"), scheme.getColor("Main Color"), scheme.getColor("Main Color"), scheme.getColor("Main Color"));
                context.getInterface().fillRect(new Rectangle(getOffField(context).x, getOffField(context).y + getOffField(context).height - 1, getOffField(context).width, 1), scheme.getColor("Main Color"), scheme.getColor("Main Color"), scheme.getColor("Main Color"), scheme.getColor("Main Color"));

                if (state)
                    context.getInterface().fillRect(new Rectangle(getOffField(context).x + 2, getOffField(context).y + 2, getOffField(context).width - 4, getOffField(context).height - 4), scheme.getColor("Main Color"), scheme.getColor("Main Color"), scheme.getColor("Main Color"), scheme.getColor("Main Color"));

            }

            @Override
            public int getDefaultHeight() {
                return getBaseHeight();
            }

            @Override
            public Rectangle getOnField(Context context) {
                return new Rectangle(context.getPos().x + context.getSize().width - 12, context.getPos().y + 3, 9, 9);
            }

            @Override
            public Rectangle getOffField(Context context) {
                return new Rectangle(context.getPos().x + context.getSize().width - 12, context.getPos().y + 3, 9, 9);
            }
        };
    }

    public ISwitchRenderer<String> getCycleSwitchRenderer (int logicalLevel, int graphicalLevel, boolean container) {
        return new ISwitchRenderer<String>() {
            @Override
            public void renderButton(Context context, String title, boolean focus, String state) {
                fillBaseRect(context,focus,false,logicalLevel,graphicalLevel,null);
                Color color=scheme.getColor("Main Color");
                if (graphicalLevel<=0 && container) {
                    context.getInterface().fillRect(new Rectangle(context.getPos().x,context.getPos().y+context.getSize().height-1,context.getSize().width,1),color,color,color,color);
                }
                renderOverlay(context);
                Color textColor=getFontColor(focus);
                context.getInterface().drawString(new Point(context.getRect().x+padding,context.getRect().y+padding),height,title+separator+state,textColor);
                Rectangle rect=getOnField(context);
                Context subContext=new Context(context,rect.width,new Point(rect.x-context.getRect().x,0),true,true);
                subContext.setHeight(rect.height);
                getSmallButtonRenderer(ITheme.LEFT,logicalLevel,graphicalLevel,container).renderButton(subContext,null,focus,null);
                rect=getOffField(context);
                subContext=new Context(context,rect.width,new Point(rect.x-context.getRect().x,0),true,true);
                subContext.setHeight(rect.height);
                getSmallButtonRenderer(ITheme.RIGHT,logicalLevel,graphicalLevel,container).renderButton(subContext,null,focus,null);
            }

            @Override
            public int getDefaultHeight() {
                return getBaseHeight();
            }

            @Override
            public Rectangle getOnField(Context context) {
                Rectangle rect=context.getRect();
                return new Rectangle(rect.x+rect.width-rect.height,rect.y,rect.height,rect.height);
            }

            @Override
            public Rectangle getOffField(Context context) {
                Rectangle rect=context.getRect();
                return new Rectangle(rect.x+rect.width-2*rect.height,rect.y,rect.height,rect.height);
            }
        };
    }

    @Override
    public int getBaseHeight() {
        return height+2*padding;
    }

    @Override
    public Color getMainColor(boolean focus, boolean active) {
        if (active) return scheme.getColor("Enabled Color");
        else return scheme.getColor("Disabled Color");
    }

    @Override
    public Color getBackgroundColor(boolean focus) {
        return scheme.getColor("Settings Color");
    }

    @Override
    public Color getFontColor(boolean focus) {
        return scheme.getColor("Font Color");
    }
}
