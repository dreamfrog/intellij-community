/*
 * Copyright 2000-2009 JetBrains s.r.o.
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
package com.intellij.packaging.impl.run;

import com.intellij.icons.AllIcons;
import com.intellij.ide.util.ElementsChooser;
import com.intellij.packaging.artifacts.Artifact;
import com.intellij.packaging.artifacts.ArtifactPointer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

/**
 * @author nik
 */
public class ArtifactChooser extends ElementsChooser<ArtifactPointer> {
  private static final Comparator<ArtifactPointer> ARTIFACT_COMPARATOR = new Comparator<ArtifactPointer>() {
    public int compare(ArtifactPointer o1, ArtifactPointer o2) {
      return o1.getArtifactName().compareToIgnoreCase(o2.getArtifactName());
    }
  };
  private static final ElementProperties INVALID_ARTIFACT_PROPERTIES = new ElementProperties() {
    public Icon getIcon() {
      return AllIcons.Nodes.Artifact;
    }

    public Color getColor() {
      return Color.RED;
    }
  };

  public ArtifactChooser(List<ArtifactPointer> pointers) {
    super(pointers, false);
    for (ArtifactPointer pointer : pointers) {
      if (pointer.getArtifact() == null) {
        setElementProperties(pointer, INVALID_ARTIFACT_PROPERTIES);
      }
    }
    sort(ARTIFACT_COMPARATOR);
  }

  @Override
  protected String getItemText(@NotNull ArtifactPointer value) {
    return value.getArtifactName();
  }

  @Override
  protected Icon getItemIcon(ArtifactPointer value) {
    final Artifact artifact = value.getArtifact();
    return artifact != null ? artifact.getArtifactType().getIcon() : null;
  }
}
