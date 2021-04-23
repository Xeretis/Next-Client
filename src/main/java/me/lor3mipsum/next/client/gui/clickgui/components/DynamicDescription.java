package me.lor3mipsum.next.client.gui.clickgui.components;

import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.theme.DescriptionRenderer;
import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.impl.modules.client.ClickGuiModule;

import java.awt.*;

public class DynamicDescription implements DescriptionRenderer {
    protected Point offset;
    protected Point pos;
    
    private final ClickGuiModule guiModule = Next.INSTANCE.moduleManager.getModule(ClickGuiModule.class);

    public DynamicDescription (Point offset, Point pos) {
        this.offset = offset;
        this.pos = pos;
    }

    @Override
    public void renderDescription(Context context) {
        if (context.getDescription() != null) {
            if(guiModule.descriptionMode.is("Mouse")) {
                Point mouseDescPos=context.getInterface().getMouse();
                mouseDescPos.translate(offset.x,offset.y);
                Rectangle r=new Rectangle(mouseDescPos,new Dimension(context.getInterface().getFontWidth(context.getDescription()),context.getInterface().getFontHeight()));
                Color bgcolor=new Color(guiModule.settingBackgroundColor.getColor().getRed(),guiModule.settingBackgroundColor.getColor().getGreen(),guiModule.settingBackgroundColor.getColor().getBlue(), (int) guiModule.opacity.getNumber());
                context.getInterface().fillRect(r,bgcolor,bgcolor,bgcolor,bgcolor);
                Color color=new Color(guiModule.backgroundColor.getColor().getRed(), guiModule.backgroundColor.getColor().getGreen(),guiModule.backgroundColor.getColor().getBlue(), (int) guiModule.opacity.getNumber());
                context.getInterface().drawRect(r,color,color,color,color);
                Color textColor = new Color(guiModule.fontColor.getColor().getRed(), guiModule.fontColor.getColor().getGreen(),guiModule.fontColor.getColor().getBlue());
                context.getInterface().drawString(mouseDescPos,context.getDescription(),textColor);
            } else {
                Rectangle r=new Rectangle(pos,new Dimension(context.getInterface().getFontWidth(context.getDescription()),context.getInterface().getFontHeight()));
                Color bgcolor=new Color(guiModule.settingBackgroundColor.getColor().getRed(),guiModule.settingBackgroundColor.getColor().getGreen(),guiModule.settingBackgroundColor.getColor().getBlue(), (int) guiModule.opacity.getNumber());
                context.getInterface().fillRect(r,bgcolor,bgcolor,bgcolor,bgcolor);
                Color color=new Color(guiModule.backgroundColor.getColor().getRed(), guiModule.backgroundColor.getColor().getGreen(),guiModule.backgroundColor.getColor().getBlue(), (int) guiModule.opacity.getNumber());
                context.getInterface().drawRect(r,color,color,color,color);
                Color textColor = new Color(guiModule.fontColor.getColor().getRed(), guiModule.fontColor.getColor().getGreen(),guiModule.fontColor.getColor().getBlue());
                context.getInterface().drawString(pos,context.getDescription(),textColor);
            }
        }
    }
}
