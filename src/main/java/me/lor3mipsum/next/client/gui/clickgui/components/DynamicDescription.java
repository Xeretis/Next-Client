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
                Color bgcolor=new Color(ClickGuiModule.INSTANCE.settingBackgroundColor.getColor().getRed(),ClickGuiModule.INSTANCE.settingBackgroundColor.getColor().getGreen(),ClickGuiModule.INSTANCE.settingBackgroundColor.getColor().getBlue(), (int) ClickGuiModule.INSTANCE.opacity.getNumber());
                context.getInterface().fillRect(r,bgcolor,bgcolor,bgcolor,bgcolor);
                Color color=new Color(ClickGuiModule.INSTANCE.backgroundColor.getColor().getRed(), ClickGuiModule.INSTANCE.backgroundColor.getColor().getGreen(),ClickGuiModule.INSTANCE.backgroundColor.getColor().getBlue(), (int) ClickGuiModule.INSTANCE.opacity.getNumber());
                context.getInterface().drawRect(r,color,color,color,color);
                Color textColor = new Color(ClickGuiModule.INSTANCE.fontColor.getColor().getRed(), ClickGuiModule.INSTANCE.fontColor.getColor().getGreen(),ClickGuiModule.INSTANCE.fontColor.getColor().getBlue());
                context.getInterface().drawString(mouseDescPos,context.getDescription(),textColor);
            } else {
                Rectangle r=new Rectangle(pos,new Dimension(context.getInterface().getFontWidth(context.getDescription()),context.getInterface().getFontHeight()));
                Color bgcolor=new Color(ClickGuiModule.INSTANCE.settingBackgroundColor.getColor().getRed(),ClickGuiModule.INSTANCE.settingBackgroundColor.getColor().getGreen(),ClickGuiModule.INSTANCE.settingBackgroundColor.getColor().getBlue(), (int) ClickGuiModule.INSTANCE.opacity.getNumber());
                context.getInterface().fillRect(r,bgcolor,bgcolor,bgcolor,bgcolor);
                Color color=new Color(ClickGuiModule.INSTANCE.backgroundColor.getColor().getRed(), ClickGuiModule.INSTANCE.backgroundColor.getColor().getGreen(),ClickGuiModule.INSTANCE.backgroundColor.getColor().getBlue(), (int) ClickGuiModule.INSTANCE.opacity.getNumber());
                context.getInterface().drawRect(r,color,color,color,color);
                Color textColor = new Color(ClickGuiModule.INSTANCE.fontColor.getColor().getRed(), ClickGuiModule.INSTANCE.fontColor.getColor().getGreen(),ClickGuiModule.INSTANCE.fontColor.getColor().getBlue());
                context.getInterface().drawString(pos,context.getDescription(),textColor);
            }
        }
    }
}
