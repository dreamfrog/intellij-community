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
package com.intellij.openapi.editor.impl.event;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.event.*;
import com.intellij.openapi.editor.ex.*;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EditorEventMulticasterImpl implements EditorEventMulticasterEx {
  private final EventDispatcher<DocumentListener> myDocumentMulticaster = EventDispatcher.create(DocumentListener.class);
  private final EventDispatcher<EditReadOnlyListener> myEditReadOnlyMulticaster = EventDispatcher.create(EditReadOnlyListener.class);

  private final EventDispatcher<EditorMouseListener> myEditorMouseMulticaster = EventDispatcher.create(EditorMouseListener.class);
  private final EventDispatcher<EditorMouseMotionListener> myEditorMouseMotionMulticaster = EventDispatcher.create(EditorMouseMotionListener.class);
  private final EventDispatcher<ErrorStripeListener> myErrorStripeMulticaster = EventDispatcher.create(ErrorStripeListener.class);
  private final EventDispatcher<CaretListener> myCaretMulticaster = EventDispatcher.create(CaretListener.class);
  private final EventDispatcher<SelectionListener> mySelectionMulticaster = EventDispatcher.create(SelectionListener.class);
  private final EventDispatcher<VisibleAreaListener> myVisibleAreaMulticaster = EventDispatcher.create(VisibleAreaListener.class);
  private final EventDispatcher<PropertyChangeListener> myPropertyChangeMulticaster = EventDispatcher.create(PropertyChangeListener.class);
  private final EventDispatcher<FocusChangeListener> myFocusChangeListenerMulticaster = EventDispatcher.create(FocusChangeListener.class);

  public void registerDocument(DocumentEx document) {
    document.addDocumentListener(myDocumentMulticaster.getMulticaster());
    document.addEditReadOnlyListener(myEditReadOnlyMulticaster.getMulticaster());
  }

  public void registerEditor(EditorEx editor) {
    editor.addEditorMouseListener(myEditorMouseMulticaster.getMulticaster());
    editor.addEditorMouseMotionListener(myEditorMouseMotionMulticaster.getMulticaster());
    ((EditorMarkupModel) editor.getMarkupModel()).addErrorMarkerListener(myErrorStripeMulticaster.getMulticaster());
    editor.getCaretModel().addCaretListener(myCaretMulticaster.getMulticaster());
    editor.getSelectionModel().addSelectionListener(mySelectionMulticaster.getMulticaster());
    editor.getScrollingModel().addVisibleAreaListener(myVisibleAreaMulticaster.getMulticaster());
    editor.addPropertyChangeListener(myPropertyChangeMulticaster.getMulticaster());
    editor.addFocusListener(myFocusChangeListenerMulticaster.getMulticaster());
  }

  @Override
  public void addDocumentListener(@NotNull DocumentListener listener) {
    myDocumentMulticaster.addListener(listener);
  }

  @Override
  public void addDocumentListener(@NotNull final DocumentListener listener, @NotNull Disposable parentDisposable) {
    addDocumentListener(listener);
    Disposer.register(parentDisposable, new Disposable() {
      @Override
      public void dispose() {
        removeDocumentListener(listener);
      }
    });
  }

  @Override
  public void removeDocumentListener(@NotNull DocumentListener listener) {
    myDocumentMulticaster.removeListener(listener);
  }

  @Override
  public void addEditorMouseListener(@NotNull EditorMouseListener listener) {
    myEditorMouseMulticaster.addListener(listener);
  }

  @Override
  public void addEditorMouseListener(@NotNull final EditorMouseListener listener, @NotNull final Disposable parentDisposable) {
    addEditorMouseListener(listener);
    Disposer.register(parentDisposable, new Disposable() {
      @Override
      public void dispose() {
        removeEditorMouseListener(listener);
      }
    });
  }

  @Override
  public void removeEditorMouseListener(@NotNull EditorMouseListener listener) {
    myEditorMouseMulticaster.removeListener(listener);
  }

  @Override
  public void addEditorMouseMotionListener(@NotNull EditorMouseMotionListener listener) {
    myEditorMouseMotionMulticaster.addListener(listener);
  }

  @Override
  public void addEditorMouseMotionListener(@NotNull final EditorMouseMotionListener listener, @NotNull final Disposable parentDisposable) {
    addEditorMouseMotionListener(listener);
    Disposer.register(parentDisposable, new Disposable() {
      @Override
      public void dispose() {
        removeEditorMouseMotionListener(listener);
      }
    });
  }

  @Override
  public void removeEditorMouseMotionListener(@NotNull EditorMouseMotionListener listener) {
    myEditorMouseMotionMulticaster.removeListener(listener);
  }

  @Override
  public void addCaretListener(@NotNull CaretListener listener) {
    myCaretMulticaster.addListener(listener);
  }

  @Override
  public void addCaretListener(@NotNull final CaretListener listener, @NotNull final Disposable parentDisposable) {
    addCaretListener(listener);
    Disposer.register(parentDisposable, new Disposable() {
      @Override
      public void dispose() {
        removeCaretListener(listener);
      }
    });
  }

  @Override
  public void removeCaretListener(@NotNull CaretListener listener) {
    myCaretMulticaster.removeListener(listener);
  }

  @Override
  public void addSelectionListener(@NotNull SelectionListener listener) {
    mySelectionMulticaster.addListener(listener);
  }

  @Override
  public void addSelectionListener(@NotNull SelectionListener listener, @NotNull Disposable parentDisposable) {
    mySelectionMulticaster.addListener(listener, parentDisposable);
  }

  @Override
  public void removeSelectionListener(@NotNull SelectionListener listener) {
    mySelectionMulticaster.removeListener(listener);
  }

  @Override
  public void addErrorStripeListener(@NotNull ErrorStripeListener listener) {
    myErrorStripeMulticaster.addListener(listener);
  }

  @Override
  public void addErrorStripeListener(@NotNull ErrorStripeListener listener, @NotNull Disposable parentDisposable) {
    myErrorStripeMulticaster.addListener(listener, parentDisposable);
  }

  @Override
  public void removeErrorStripeListener(@NotNull ErrorStripeListener listener) {
    myErrorStripeMulticaster.removeListener(listener);
  }

  @Override
  public void addVisibleAreaListener(@NotNull VisibleAreaListener listener) {
    myVisibleAreaMulticaster.addListener(listener);
  }

  @Override
  public void removeVisibleAreaListener(@NotNull VisibleAreaListener listener) {
    myVisibleAreaMulticaster.removeListener(listener);
  }

  @Override
  public void addEditReadOnlyListener(@NotNull EditReadOnlyListener listener) {
    myEditReadOnlyMulticaster.addListener(listener);
  }

  @Override
  public void removeEditReadOnlyListener(@NotNull EditReadOnlyListener listener) {
    myEditReadOnlyMulticaster.removeListener(listener);
  }

  @Override
  public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {
    myPropertyChangeMulticaster.addListener(listener);
  }

  @Override
  public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {
    myPropertyChangeMulticaster.removeListener(listener);
  }

  @Override
  public void addFocusChangeListner(@NotNull FocusChangeListener listener) {
    myFocusChangeListenerMulticaster.addListener(listener);
  }

  @Override
  public void addFocusChangeListner(@NotNull FocusChangeListener listener, @NotNull Disposable parentDisposable) {
    myFocusChangeListenerMulticaster.addListener(listener,parentDisposable);
  }

  @Override
  public void removeFocusChangeListner(@NotNull FocusChangeListener listener) {
    myFocusChangeListenerMulticaster.removeListener(listener);
  }

  @TestOnly
  public Map<Class, List> getListeners() {
    Map<Class, List> myCopy = new LinkedHashMap<Class, List>();
    myCopy.put(DocumentListener.class, new ArrayList<DocumentListener>(myDocumentMulticaster.getListeners()));
    myCopy.put(EditReadOnlyListener.class, new ArrayList<EditReadOnlyListener>(myEditReadOnlyMulticaster.getListeners()));

    myCopy.put(EditorMouseListener.class, new ArrayList<EditorMouseListener>(myEditorMouseMulticaster.getListeners()));
    myCopy.put(EditorMouseMotionListener.class, new ArrayList<EditorMouseMotionListener>(myEditorMouseMotionMulticaster.getListeners()));
    myCopy.put(ErrorStripeListener.class, new ArrayList<ErrorStripeListener>(myErrorStripeMulticaster.getListeners()));
    myCopy.put(CaretListener.class, new ArrayList<CaretListener>(myCaretMulticaster.getListeners()));
    myCopy.put(SelectionListener.class, new ArrayList<SelectionListener>(mySelectionMulticaster.getListeners()));
    myCopy.put(VisibleAreaListener.class, new ArrayList<VisibleAreaListener>(myVisibleAreaMulticaster.getListeners()));
    myCopy.put(PropertyChangeListener.class, new ArrayList<PropertyChangeListener>(myPropertyChangeMulticaster.getListeners()));
    myCopy.put(FocusChangeListener.class, new ArrayList<FocusChangeListener>(myFocusChangeListenerMulticaster.getListeners()));
    return myCopy;
  }
}
