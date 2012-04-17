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
package com.intellij.android.designer.model.table;

import com.android.ide.common.rendering.api.ViewInfo;
import com.intellij.android.designer.model.RadViewComponent;
import com.intellij.android.designer.model.RadViewContainer;
import com.intellij.designer.model.RadComponent;
import com.intellij.util.ArrayUtil;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Alexander Lobas
 */
public class RadTableLayoutComponent extends RadViewContainer {
  private GridInfo myGridInfo;
  private GridInfo myVirtualGridInfo;

  @Override
  public void setViewInfo(ViewInfo viewInfo) {
    super.setViewInfo(viewInfo);
    myGridInfo = null;
    myVirtualGridInfo = null;
  }

  private int[] getColumnWidths() {
    try {
      Object viewObject = myViewInfo.getViewObject();
      Class<?> viewClass = viewObject.getClass();
      Field maxWidths = viewClass.getDeclaredField("mMaxWidths");
      maxWidths.setAccessible(true);
      int[] columnWidths = (int[])maxWidths.get(viewObject);
      return columnWidths == null ? ArrayUtil.EMPTY_INT_ARRAY : columnWidths;
    }
    catch (Throwable e) {
      return ArrayUtil.EMPTY_INT_ARRAY;
    }
  }

  public GridInfo getGridInfo() {
    if (myGridInfo == null) {
      myGridInfo = new GridInfo();

      int[] columnWidths = getColumnWidths();
      if (columnWidths.length > 0) {
        myGridInfo.emptyColumns = new boolean[columnWidths.length];
        myGridInfo.vLines = new int[columnWidths.length + 1];

        for (int i = 0; i < columnWidths.length; i++) {
          int width = Math.max(columnWidths[i], 0);
          myGridInfo.emptyColumns[i] = width == 0;

          if (width == 0) {
            width = 2;
            if (i + 1 < columnWidths.length) {
              columnWidths[i + 1] -= width;
            }
          }

          myGridInfo.width += width;
          myGridInfo.vLines[i + 1] = myGridInfo.width;
        }
      }

      List<RadComponent> rows = getChildren();
      if (!rows.isEmpty()) {
        Rectangle bounds = getBounds();
        if (columnWidths.length == 0) {
          myGridInfo.width = bounds.width;
        }

        myGridInfo.hLines = new int[rows.size() + 1];
        int index = 1;
        for (RadComponent row : rows) {
          Rectangle rowBounds = row.getBounds();
          myGridInfo.hLines[index++] = myGridInfo.height = rowBounds.y - bounds.y + rowBounds.height;
        }
      }
    }
    return myGridInfo;
  }

  public GridInfo getVirtualGridInfo() {
    if (myVirtualGridInfo == null) {
      myVirtualGridInfo = new GridInfo();
      GridInfo gridInfo = getGridInfo();
      Rectangle bounds = getBounds();

      myVirtualGridInfo.width = bounds.width;
      myVirtualGridInfo.height = bounds.height;

      myVirtualGridInfo.vLines = addLineInfo(gridInfo.vLines, bounds.width - (gridInfo.vLines.length == 0 ? 0 : gridInfo.width));
      myVirtualGridInfo.hLines = addLineInfo(gridInfo.hLines, bounds.height - gridInfo.height);

      List<RadComponent> rows = getChildren();
      if (!rows.isEmpty()) {
        int columnSize = Math.max(1, gridInfo.vLines.length - 1);
        RadComponent[][] components = new RadComponent[rows.size()][columnSize];
        myVirtualGridInfo.components = components;

        for (int i = 0; i < components.length; i++) {
          RadComponent row = rows.get(i);

          if (RadTableRowLayout.is(row)) {
            int index = 0;
            for (RadComponent column : row.getChildren()) {
              int cellIndex = getCellIndex(column);
              if (cellIndex > index) {
                index = cellIndex;
              }

              int cellSpan = getCellSnap(column);
              for (int j = 0; j < cellSpan; j++) {
                components[i][index++] = column;
              }
            }
          }
          else {
            components[i][0] = row;
          }
        }
      }
    }
    return myVirtualGridInfo;
  }

  private static int getCellIndex(RadComponent component) {
    try {
      String column = ((RadViewComponent)component).getTag().getAttributeValue("android:layout_column");
      return Integer.parseInt(column);
    }
    catch (Throwable e) {
      return -1;
    }
  }

  private static int getCellSnap(RadComponent component) {
    try {
      String span = ((RadViewComponent)component).getTag().getAttributeValue("android:layout_span");
      return Integer.parseInt(span);
    }
    catch (Throwable e) {
      return 1;
    }
  }

  private static final int NEW_CELL_SIZE = 32;

  private static int[] addLineInfo(int[] oldLines, int delta) {
    if (delta > 0) {
      int newLength = oldLines.length + delta / NEW_CELL_SIZE;

      if (newLength > oldLines.length) {
        int[] newLines = new int[newLength];
        int startIndex = oldLines.length;

        if (oldLines.length > 0) {
          System.arraycopy(oldLines, 0, newLines, 0, oldLines.length);
        }
        else {
          startIndex = 1;
        }

        for (int i = startIndex; i < newLength; i++) {
          newLines[i] = newLines[i - 1] + NEW_CELL_SIZE;
        }

        return newLines;
      }
      return oldLines;
    }
    return oldLines;
  }
}