package me.lor3mipsum.next.client.gui.clickgui.themes;

import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.theme.*;
import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.impl.modules.client.ClickGuiModule;

import java.awt.*;

public class NextTheme implements Theme {
    protected ColorScheme scheme;
    protected Renderer componentRenderer, containerRenderer, panelRenderer;
    boolean line;
    boolean outline;

    public NextTheme (ColorScheme scheme, int height, boolean line, boolean outline) {
        this.line = line;
        this.outline = outline;
        this.scheme=scheme;
        panelRenderer = new ComponentRenderer(0,height,2);
        containerRenderer = new ComponentRenderer(1, height, 2);
        componentRenderer = new ComponentRenderer(2,height,2);
    }

    @Override
    public Renderer getPanelRenderer() {
        return panelRenderer;
    }

    @Override
    public Renderer getContainerRenderer() {
        return containerRenderer;
    }

    @Override
    public Renderer getComponentRenderer() {
        return componentRenderer;
    }

    protected class ComponentRenderer extends RendererBase {
        int level;

        public ComponentRenderer (int level, int height, int border) {
            super(height+2*border,border,0,0,0);
            this.level=level;
        }

        @Override
        public void renderTitle(Context context, String text, boolean focus, boolean active, boolean open) {
            super.renderTitle(context,text,focus,active,open);
            if (level!=0) {
                Color color=getFontColor(active);
                Point p = new Point(context.getPos().x+context.getSize().width-context.getSize().height*3/4,context.getPos().y+3);
                if (open) {
                    context.getInterface().drawString(p, "-", color);
                } else  {
                    context.getInterface().drawString(p, "+", color);
                }
            }
            if (level==0 && line) {
                Color color= new Color(getDefaultColorScheme().getActiveColor().getRed(), getDefaultColorScheme().getActiveColor().getGreen(), getDefaultColorScheme().getActiveColor().getBlue(), getDefaultColorScheme().getOpacity());
                context.getInterface().fillRect(new Rectangle(context.getRect().x,context.getRect().y+context.getRect().height-1,context.getRect().width,3),color,color,color,color);
            }
        }

        @Override
        public void renderRect (Context context, String text, boolean focus, boolean active, Rectangle rectangle, boolean overlay) {
            Color color=getMainColor(focus,active);
            rectangle.height += 2;
            context.getInterface().fillRect(rectangle,color,color,color,color);
            if (level!=0 && overlay) {
                Color overlayColor = Next.INSTANCE.moduleManager.getModule(ClickGuiModule.class).highlightColor.getColor();
                if (!context.isHovered()) {
                    overlayColor=new Color(0,0,0,0);
                }
                Rectangle overlayRect = context.getRect();
                overlayRect.height += 2;
                context.getInterface().fillRect(overlayRect,overlayColor,overlayColor,overlayColor,overlayColor);
            }
            Point stringPos=new Point(rectangle.getLocation());
            stringPos.translate(0,getOffset());
            if (level==0) stringPos=new Point(rectangle.x+rectangle.width/2-context.getInterface().getFontWidth(text)/2,rectangle.y+getOffset());
            if (level==2 && overlay) context.getInterface().drawString(stringPos,"> "+text,getFontColor(focus));
            else context.getInterface().drawString(stringPos,text,getFontColor(focus));
        }

        @Override
        public void renderBackground (Context context, boolean focus) {
            Color color = getBackgroundColor(focus);
            context.getInterface().fillRect(context.getRect(), color, color, color, color);
        }

        @Override
        public void renderBorder (Context context, boolean focus, boolean active, boolean open) {
            Color color = new Color(getDefaultColorScheme().getOutlineColor().getRed(), getDefaultColorScheme().getOutlineColor().getGreen(), getDefaultColorScheme().getOutlineColor().getBlue(), getDefaultColorScheme().getOpacity());
            if (level==1 && open && outline) {
                context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x,context.getPos().y+context.getSize().height-1),new Dimension(context.getSize().width,3)),color,color,color,color);
                context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x,context.getPos().y+getHeight(open)-1),new Dimension(context.getSize().width,3)),color,color,color,color);
            }
        }

        @Override
        public Color getMainColor (boolean focus, boolean active) {
            Color color;
            if (active && level != 0) color=getColorScheme().getActiveColor();
            else color=getColorScheme().getBackgroundColor();
            if (!active && level == 1) color=getColorScheme().getInactiveColor();
            color=new Color(color.getRed(),color.getGreen(),color.getBlue(),getColorScheme().getOpacity());
            return color;
        }

        @Override
        public Color getBackgroundColor (boolean focus) {
            Color color=getColorScheme().getBackgroundColor();
            return new Color(color.getRed(),color.getGreen(),color.getBlue(), getColorScheme().getOpacity());
        }

        @Override
        public ColorScheme getDefaultColorScheme() {
            return NextTheme.this.scheme;
        }
    }
}
