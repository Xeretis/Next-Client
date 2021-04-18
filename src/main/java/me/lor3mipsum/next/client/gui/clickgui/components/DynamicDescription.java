package me.lor3mipsum.next.client.gui.clickgui.components;

import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.theme.DescriptionRenderer;
import me.lor3mipsum.next.client.impl.modules.client.ClickGuiModule;

import java.awt.*;

public class DynamicDescription implements DescriptionRenderer {
    protected Point offset;
    protected Point pos;

    public DynamicDescription (Point offset, Point pos) {
        this.offset = offset;
        this.pos = pos;
    }

    @Override
    public void renderDescription(Context context) {
        if (context.getDescription() != null) {
            if(ClickGuiModule.INSTANCE.descriptionMode.is("Mouse")) {
                Point mouseDescPos=context.getInterface().getMouse();
                mouseDescPos.translate(offset.x,offset.y);
                Rectangle r=new Rectangle(mouseDescPos,new Dimension(context.getInterface().getFontWidth(context.getDescription()),context.getInterface().getFontHeight()));
                Color bgcolor=new Color(0,0,0);
                context.getInterface().fillRect(r,bgcolor,bgcolor,bgcolor,bgcolor);
                Color color=new Color(255,255,255);
                context.getInterface().drawRect(r,color,color,color,color);
                context.getInterface().drawString(mouseDescPos,context.getDescription(),color);
            } else {
                Rectangle r=new Rectangle(pos,new Dimension(context.getInterface().getFontWidth(context.getDescription()),context.getInterface().getFontHeight()));
                Color bgcolor=new Color(0,0,0);
                context.getInterface().fillRect(r,bgcolor,bgcolor,bgcolor,bgcolor);
                Color color=new Color(255,255,255);
                context.getInterface().drawRect(r,color,color,color,color);
                context.getInterface().drawString(pos,context.getDescription(),color);
            }
        }
    }
}
