/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import aQute.bnd.build.Container;
import aQute.bnd.build.Project;
import aQute.bnd.build.ProjectBuilder;
import aQute.bnd.build.Workspace;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.FileResource;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Processor;
import aQute.bnd.service.AnalyzerPlugin;
import aQute.libg.generics.Create;

/**
 * This script runs after the bnd file stuff has been done, before analyzing any
 * classes. It will check if the bnd file contains -pack (the bnd file must
 * contain it, not a parent). It will then pack all projects listed as its
 * valued. For each project, a bnd file is created that has no longer references
 * to the build. All dependent JAR files are stored in the jar directory for
 * this purpose. Additionally, a runtests script is added and the bnd jar is
 * included to make the tess self contained.
 */

public class Packaging implements AnalyzerPlugin {
	private final static String PACK = "-pack";

	public boolean analyzeJar(Analyzer analyzer) throws Exception {
		if (!(analyzer instanceof ProjectBuilder))
			return false;

		// Make sure -pack is set in the actual file or one of its includes
		String pack = analyzer.getProperty(PACK);
		if (pack == null)
			return false;

		Map<String,String> fileToPath = Create.map();

		ProjectBuilder pb = (ProjectBuilder) analyzer;
		Workspace workspace = pb.getProject().getWorkspace();
		Jar jar = analyzer.getJar();

		// For each param listed ...
		Parameters params = pb.parseHeader(pack);
		if (params.isEmpty()) {
			analyzer.warning("No items to pack");
			return false;
		}

		for (String entry : params.keySet()) {
			try {
				Project project = workspace.getProject(entry);
				if (project != null) {
					pack(analyzer, jar, project, null, fileToPath);
				} else {
					flatten(analyzer, null, jar,
							new File(Processor.removeDuplicateMarker(entry)),
							Collections.<String, String> emptyMap(), true,
							fileToPath);
				}
			} catch (Exception t) {
				analyzer.error("While packaging %s got %s", entry, t);
				throw t;
			}
		}
		return false;
	}

	/**
	 * Store a project in a JAR so that we can later unzip this project and have
	 * all information.
	 *
	 * @param jar
	 * @param project
	 * @throws Exception
	 */
	protected void pack(Analyzer analyzer, Jar jar, Project project,
			Collection<Container> sharedRunpath, Map<String,String> fileToPath)
			throws Exception {

		/**
		 * Add all sub bundles to the -runbundles so they are installed We
		 * assume here that the project is build ahead of time.
		 */
		File[] files = project.getBuildFiles();
		if (files == null) {
			System.out.println("Project has no build files " + project);
			return;
		}

		flatten(analyzer, null, jar, project,
				Collections.<String, String> emptyMap(), true, fileToPath);
	}

	protected void flatten(Analyzer analyzer, StringBuilder sb, Jar jar,
			Collection<Container> path, boolean store,
			Map<String,String> fileToPath) throws Exception {
		for (Container container : path) {
			flatten(analyzer, sb, jar, container, store, fileToPath);
		}
		if (sb != null)
			sb.deleteCharAt(sb.length() - 2);
	}

	protected void flatten(Analyzer analyzer, StringBuilder sb, Jar jar,
			Container container, boolean store, Map<String,String> fileToPath)
			throws Exception {
		switch (container.getType()) {
			case LIBRARY :
				flatten(analyzer, sb, jar, container.getMembers(), store,
						fileToPath);
				return;

			case PROJECT :
				flatten(analyzer, sb, jar, container.getProject(),
						container.getAttributes(), store, fileToPath);
				break;

			case EXTERNAL :
				flatten(analyzer, sb, jar, container.getFile(),
						container.getAttributes(), store, fileToPath);
				break;

			case REPO :
				flatten(analyzer, sb, jar, container.getFile(),
						container.getAttributes(), store, fileToPath);
				break;
			default :
				analyzer.error("Unrecognized container type: %s",
						container.getType());
				break;
		}
	}

	protected void flatten(Analyzer analyzer, StringBuilder sb, Jar jar,
			Project project, Map<String,String> map, boolean store,
			Map<String,String> fileToPath) throws Exception {
		File[] subs = project.getBuildFiles();
		analyzer.getInfo(project);
		if (subs == null) {
			analyzer.error("Project cannot build %s ", project);
		} else
			for (File sub : subs)
				flatten(analyzer, sb, jar, sub, map, store, fileToPath);
	}

	protected void flatten(Analyzer analyzer, StringBuilder sb, Jar jar,
			File sub, Map<String,String> map, boolean store,
			Map<String,String> fileToPath) throws Exception {
		sub = getOriginalFile(sub);
		String path = fileToPath.get(sub.getAbsolutePath());
		if (path == null) {
			path = "jar/" + new BundleInfo(analyzer, sub).canonicalName();
			fileToPath.put(sub.getAbsolutePath(), path);
		}
		if (store && (jar.getResource(path) == null)) {
			jar.putResource(path, new FileResource(sub));
		}
		if (sb != null) {
			sb.append("\\\n    ");
			sb.append(path);
			sb.append(";version=file");
			for (Map.Entry<String,String> entry : map.entrySet()) {
				if (!entry.getKey().equals("version")) {
					sb.append(";");
					sb.append(entry.getKey());
					sb.append("=\"");
					sb.append(entry.getValue());
					sb.append("\"");
				}
			}
			sb.append(", ");
		}
	}

	private File getOriginalFile(File file) {
		// file has source attached
		if (file.getName().startsWith("+") && file.exists()) {
			File originalFile = new File(file.getParentFile(),
					file.getName().substring(1));
			if (originalFile.exists()) {
				return originalFile;
			}
		}
		return file;
	}

	protected void addNotice(StringBuilder sb) {
		sb.append("# Copyright (c) Contributors to the Eclipse Foundation\n");
		sb.append("#\n");
		sb.append(
				"# Licensed under the Apache License, Version 2.0 (the \"License\");\n");
		sb.append(
				"# you may not use this file except in compliance with the License.\n");
		sb.append("# You may obtain a copy of the License at\n");
		sb.append("#\n");
		sb.append("#      https://www.apache.org/licenses/LICENSE-2.0\n");
		sb.append("#\n");
		sb.append(
				"# Unless required by applicable law or agreed to in writing, software\n");
		sb.append(
				"# distributed under the License is distributed on an \"AS IS\" BASIS,\n");
		sb.append(
				"# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n");
		sb.append(
				"# See the License for the specific language governing permissions and\n");
		sb.append("# limitations under the License.\n");
		sb.append("#\n");
		sb.append("# SPDX-License-Identifier: Apache-2.0\n");
		sb.append("\n");
	}
}
