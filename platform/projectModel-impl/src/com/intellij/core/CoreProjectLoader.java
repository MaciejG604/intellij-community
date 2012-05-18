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
package com.intellij.core;

import com.intellij.mock.MockProject;
import com.intellij.openapi.components.ComponentManager;
import com.intellij.openapi.components.PathMacroManager;
import com.intellij.openapi.components.impl.stores.StorageData;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author yole
 */
public class CoreProjectLoader {
  public static void loadProject(MockProject project, @NotNull VirtualFile virtualFile) throws IOException, JDOMException {
    if (virtualFile.isDirectory() && virtualFile.findChild(".idea") != null) {
      project.setBaseDir(virtualFile);
      loadDirectoryProject(project, virtualFile);
    }
    else {
      // TODO load .ipr
      throw new UnsupportedOperationException();
    }
  }

  private static void loadDirectoryProject(Project project, VirtualFile projectDir) throws IOException, JDOMException {
    VirtualFile dotIdea = projectDir.findChild(".idea");
    VirtualFile modulesXml = dotIdea.findChild("modules.xml");
    StorageData storageData = loadStorageFile(project, modulesXml);
    final Element moduleManagerState = storageData.getState("ProjectModuleManager");
    if (moduleManagerState == null) {
      throw new JDOMException("cannot find ProjectModuleManager state in modules.xml");
    }
    final CoreModuleManager moduleManager = (CoreModuleManager)ModuleManager.getInstance(project);
    moduleManager.loadState(moduleManagerState);
    moduleManager.loadModules();
  }

  public static StorageData loadStorageFile(ComponentManager componentManager, VirtualFile modulesXml) throws JDOMException, IOException {
    final Document document = JDOMUtil.loadDocument(new ByteArrayInputStream(modulesXml.contentsToByteArray()));
    StorageData storageData = new StorageData("project");
    final Element element = document.getRootElement();
    PathMacroManager.getInstance(componentManager).expandPaths(element);
    storageData.load(element);
    return storageData;
  }
}
