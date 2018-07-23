.class public Opt

.super java/lang/Object

.method public <init>()V
  .limit locals 1
  .limit stack 1
  aload_0
  invokenonvirtual java/lang/Object/<init>()V
  return
.end method

.method public foo()V
  .limit locals 2
  .limit stack 2
  ldc "test"
  dup
  astore_1
  aconst_null
  if_acmpeq true_1
  iconst_0
  goto stop_2
  true_1:
  iconst_1
  stop_2:
  ifeq stop_0
  return
  stop_0:
  ldc "dead"
  dup
  astore_1
  pop
  return
.end method

