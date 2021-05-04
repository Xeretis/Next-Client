package me.lor3mipsum.next.client.impl.events;

import me.lor3mipsum.next.client.event.events.Cancellable;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class EntityRenderEvent extends Cancellable {
    public static class Single extends EntityRenderEvent {
        protected Entity entity;
        protected MatrixStack matrix;
        protected VertexConsumerProvider vertex;

        public Entity getEntity() {
            return entity;
        }

        public MatrixStack getMatrix() {
            return matrix;
        }

        public VertexConsumerProvider getVertex() {
            return vertex;
        }

        public static class Pre extends Single {
            public Pre(Entity entity, MatrixStack matrix, VertexConsumerProvider vertex) {
                this.entity = entity;
                this.matrix = matrix;
                this.vertex = vertex;
            }

            public void setMatrix(MatrixStack matrix) {
                this.matrix = matrix;
            }

            public void setVertex(VertexConsumerProvider vertex) {
                this.vertex = vertex;
            }

            public void setEntity(Entity entity) {
                this.entity = entity;
            }
        }

        public static class Post extends Single {
            public Post(Entity entity, MatrixStack matrix, VertexConsumerProvider vertex) {
                this.entity = entity;
                this.matrix = matrix;
                this.vertex = vertex;
            }
        }

        public static class Label extends Single {
            public Label(Entity entity, MatrixStack matrix, VertexConsumerProvider vertex) {
                this.entity = entity;
                this.matrix = matrix;
                this.vertex = vertex;
            }

            public void setMatrix(MatrixStack matrix) {
                this.matrix = matrix;
            }

            public void setVertex(VertexConsumerProvider vertex) {
                this.vertex = vertex;
            }
        }
    }

    public static class PreAll extends EntityRenderEvent {
    }

    public static class PostAll extends EntityRenderEvent {
    }
}
