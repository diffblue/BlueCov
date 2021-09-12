/**
 * Copyright 2016-2021 Diffblue Ltd and contributors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cprover.coverage.benchmarks;

public class ExprToken {

  public static int VALUE;

  static {
    VALUE = 10;
  }

  private final Type type;
  private final Object value;

  public ExprToken(Type t, Object val) {
    type = t;
    value = val;
  }

  public boolean IsOperator() {
    return type == Type.Operator;
  }

  public boolean IsBracket() {
    return type == Type.Bracket;
  }

  public boolean IsWord() {
    return type == Type.Word;
  }

  public char GetSymbol() {
    return (Character) value;
  }

  public char GetBracket() {
    final boolean isBracket = IsBracket();
    if (!isBracket) {
      return '\0';
    }
    return (Character) value;
  }

  public char GetOperator() {
    return (Character) value;
  }

  public String GetWord() {
    return (String) value;
  }

  @Override
  public String toString() {
    return value.toString();
  }

  @Override
  public boolean equals(Object obj) {
    ExprToken b = (ExprToken) obj;
    return type == b.type && value.equals(b.value);
  }

  public enum Type {
    Operator, Bracket, Word;
  }
}