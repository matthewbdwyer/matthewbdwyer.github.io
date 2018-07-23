.class public Control

.super java/lang/Object

.method public <init>()V
  .limit locals 1
  .limit stack 1
  aload_0
  invokenonvirtual java/lang/Object/<init>()V
  return
.end method

.method public static main([Ljava/lang/String;)V
  .limit locals 7
  .limit stack 4

;;  a = x < y;
  iload_1
  iload_2
  if_icmplt true_0
  iconst_0
  goto stop_1
  true_0:
  iconst_1
  stop_1:
  dup
  istore 4
  pop

;;  b = z != y; 
  iload_3
  iload_2
  if_icmpne true_2
  iconst_0
  goto stop_3
  true_2:
  iconst_1
  stop_3:
  dup
  istore 5
  pop

;;  a = !a;
  iload 4
  ifeq true_4
  iconst_0
  goto stop_5
  true_4:
  iconst_1
  stop_5:
  dup
  istore 4
  pop

;;  b = b || a;
  iload 5
  dup
  ifne true_6
  pop
  iload 4
  true_6:
  dup
  istore 5
  pop

;;  s = "Final is " + b;
  ldc "Final is "
  dup
  ifnull null_7
  goto stop_8
  null_7:
  pop
  ldc "null"
  stop_8:
  new java/lang/Boolean
  dup
  iload 5
  invokenonvirtual java/lang/Boolean/<init>(Z)V
  invokevirtual java/lang/Boolean/toString()Ljava/lang/String;
  invokevirtual java/lang/String/concat(Ljava/lang/String;)Ljava/lang/String;
  dup
  astore 6
  pop

  return
.end method

