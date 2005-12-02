/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package com.intellij.util.xml.reflect;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.impl.MethodSignature;

/**
 * @author peter
 */
public interface DomAttributeChildDescription extends DomChildrenDescription{
  GenericAttributeValue getDomAttributeValue(DomElement parent);

  MethodSignature getGetterMethod();
}
