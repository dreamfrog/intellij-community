package org.jetbrains.plugins.groovy.lang.psi.controlFlow.impl;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.groovy.lang.psi.controlFlow.Instruction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ven
 */
class InstructionImpl implements Instruction {
  List<InstructionImpl> myPred = new ArrayList<InstructionImpl>();

  List<InstructionImpl> mySucc = new ArrayList<InstructionImpl>();

  PsiElement myPsiElement;
  private int myNumber;

  @Nullable
  public PsiElement getElement() {
    return myPsiElement;
  }

  InstructionImpl(PsiElement psiElement, int num) {
    myPsiElement = psiElement;
    myNumber = num;
  }

  public Iterable<? extends Instruction> succ() {
    return mySucc;
  }

  public Iterable<? extends Instruction> pred() {
    return myPred;
  }

  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append(myNumber);
    builder.append("(");
    for (InstructionImpl instruction : mySucc) {
      builder.append(instruction.myNumber);
    }
    builder.append(")");
    builder.append(" element: ").append(myPsiElement);
    return builder.toString();
  }
}
