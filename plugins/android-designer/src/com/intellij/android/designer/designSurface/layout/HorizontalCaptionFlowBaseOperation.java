/*
 * Copyright 2000-2012 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.android.designer.designSurface.layout;

import com.intellij.android.designer.model.ModelParser;
import com.intellij.android.designer.model.RadViewComponent;
import com.intellij.android.designer.model.table.RadCaptionTableColumn;
import com.intellij.android.designer.model.table.RadTableLayoutComponent;
import com.intellij.android.designer.model.table.RadTableRowLayout;
import com.intellij.designer.designSurface.EditableArea;
import com.intellij.designer.designSurface.FeedbackLayer;
import com.intellij.designer.designSurface.FlowBaseOperation;
import com.intellij.designer.designSurface.OperationContext;
import com.intellij.designer.designSurface.feedbacks.LineInsertFeedback;
import com.intellij.designer.model.RadComponent;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Lobas
 */
public class HorizontalCaptionFlowBaseOperation extends FlowBaseOperation {
  private final RadTableLayoutComponent myTableComponent;
  private final EditableArea myMainArea;
  private LineInsertFeedback myMainInsertFeedback;
  private int myMainYLocation;

  public HorizontalCaptionFlowBaseOperation(RadTableLayoutComponent tableComponent,
                                            RadComponent container,
                                            OperationContext context,
                                            EditableArea mainArea) {
    super(container, context, true);
    myTableComponent = tableComponent;
    myMainArea = mainArea;
  }

  @Override
  protected void createFeedback() {
    super.createFeedback();

    if (myMainInsertFeedback == null) {
      FeedbackLayer layer = myMainArea.getFeedbackLayer();

      Rectangle bounds = myTableComponent.getBounds(layer);
      myMainYLocation = bounds.y;

      List<RadComponent> children = myTableComponent.getChildren();
      Rectangle lastChildBounds = children.get(children.size() - 1).getBounds(layer);

      myMainInsertFeedback = new LineInsertFeedback(Color.green, false);
      myMainInsertFeedback.size(0, lastChildBounds.y + lastChildBounds.height - myMainYLocation);

      layer.add(myMainInsertFeedback);
      layer.repaint();
    }
  }

  @Override
  public void showFeedback() {
    super.showFeedback();
    Point location = SwingUtilities.convertPoint(myInsertFeedback.getParent(),
                                                 myInsertFeedback.getLocation(),
                                                 myMainArea.getFeedbackLayer());
    myMainInsertFeedback.setLocation(location.x, myMainYLocation);
  }

  @Override
  public void eraseFeedback() {
    super.eraseFeedback();
    if (myMainInsertFeedback != null) {
      FeedbackLayer layer = myMainArea.getFeedbackLayer();
      layer.remove(myMainInsertFeedback);
      layer.repaint();
      myMainInsertFeedback = null;
    }
  }

  @Override
  protected void execute(@Nullable RadComponent insertBefore) throws Exception {
    List<RadComponent> rows = myTableComponent.getChildren();
    RadComponent[][] components = myTableComponent.getGridComponents(false);

    for (int i = 0; i < components.length; i++) {
      RadViewComponent container = (RadViewComponent)rows.get(i);

      if (RadTableRowLayout.is(container)) {
        RadComponent[] rowComponents = components[i];

        List<RadViewComponent> editComponents = new ArrayList<RadViewComponent>();
        for (RadComponent component : myComponents) {
          int column = ((RadCaptionTableColumn)component).getIndex();
          RadViewComponent editComponent = (RadViewComponent)rowComponents[column];
          if (editComponent != null) {
            editComponents.add(editComponent);
          }
        }

        if (editComponents.isEmpty()) {
          continue;
        }

        RadViewComponent insertBeforeColumn = null;
        if (insertBefore != null) {
          int column = ((RadCaptionTableColumn)insertBefore).getIndex();
          for (int j = column; j < rowComponents.length; j++) {
            insertBeforeColumn = (RadViewComponent)rowComponents[j];
            if (insertBeforeColumn != null) {
              if (!editComponents.isEmpty() && insertBeforeColumn == editComponents.get(0)) {
                editComponents.remove(0);
                insertBeforeColumn = null;
                continue;
              }
              break;
            }
          }
        }

        if (insertBefore == null || insertBeforeColumn != null) {
          for (RadViewComponent component : editComponents) {
            if (component != insertBeforeColumn) {
              ModelParser.moveComponent(container, component, insertBeforeColumn);
            }
          }
        }
      }
    }

    for (RadComponent component : myComponents) {
      component.removeFromParent();
      myContainer.add(component, insertBefore);
    }

    List<RadComponent> columns = myContainer.getChildren();
    int size = columns.size();
    for (int i = 0; i < size; i++) {
      int index = ((RadCaptionTableColumn)columns.get(i)).getIndex();

      for (int j = 0; j < components.length; j++) {
        if (RadTableRowLayout.is(rows.get(j))) {
          RadComponent cellComponent = components[j][index];
          if (cellComponent != null) {
            RadTableLayoutComponent.setCellIndex(cellComponent, i);
          }
        }
      }
    }
  }
}