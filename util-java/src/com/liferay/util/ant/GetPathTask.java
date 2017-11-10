/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.util.ant;

import java.io.File;
import java.io.IOException;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * @author Lily Chi
 */
public class GetPathTask extends Task {

	@Override
	public void execute() throws BuildException {
		String baseDIR = _rootDir;
		String[] classNames = _classNames.split(",");
		StringBuilder filePropertyValue = new StringBuilder();
		List<String> names = Arrays.asList(classNames);
		List classResultList = new ArrayList();
		List srcResultList = new ArrayList();

		findFiles(baseDIR, names, classResultList, srcResultList);

		if (!classResultList.isEmpty() && !srcResultList.isEmpty()) {
			for (int i = 0; i < classResultList.size(); i++) {
				filePropertyValue.append("[");
				filePropertyValue.append(
					String.valueOf(classResultList.get(i)));
				filePropertyValue.append("#");

				for (int j = 0; j < srcResultList.size(); j++) {
					String classPath = String.valueOf(classResultList.get(i));
					String srcPath = String.valueOf(srcResultList.get(j));

					if (_ifMathch(srcPath, classPath)) {
						int endIndex = srcPath.indexOf("src");

						filePropertyValue.append(
							srcPath.substring(0, endIndex));
					}
				}

				filePropertyValue.append("],");
			}

			filePropertyValue = filePropertyValue.deleteCharAt(
				filePropertyValue.lastIndexOf(","));

			getProject().setProperty(_filePaths, filePropertyValue.toString());
		}
	}

	public void findFiles(
		String baseDirPath, List<String> targetNames, List<File> classFileList,
		List<File> srcFileList) {

		File baseDir = new File(baseDirPath);

		if (!baseDir.exists() || !baseDir.isDirectory()) {
			return;
		}

		try {
			Files.walkFileTree(
				baseDir.toPath(),
				new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult preVisitDirectory(
							Path file, BasicFileAttributes attrs)
						throws IOException {

						Path absoluteNamePath = file.toAbsolutePath();

						String fileName = absoluteNamePath.toString();

						if (_ifSkipDirectory(fileName)) {
							return FileVisitResult.SKIP_SUBTREE;
						}
						else {
							return FileVisitResult.CONTINUE;
						}
					}

					@Override
					public FileVisitResult visitFile(
							Path file, BasicFileAttributes attrs)
						throws IOException {

						Path fileNamePath = file.getFileName();

						String fileName = fileNamePath.toString();

						for (String targetFileName : targetNames) {
							String targetClassName = targetFileName + ".class";
							String targetSrcName = targetFileName + ".java";

							if (targetClassName.equals(fileName)) {
								classFileList.add(file.toFile());
							}
							else if (targetSrcName.equals(fileName)) {
								srcFileList.add(file.toFile());
							}
						}

						return FileVisitResult.CONTINUE;
					}

				});
		}
		catch (IOException ioe) {
			throw new BuildException(ioe);
		}
	}

	public void setclassNames(String classNames) {
		_classNames = classNames;
	}

	public void setFilePaths(String filePaths) {
		_filePaths = filePaths;
	}

	public void setRootDir(String rootDir) {
		_rootDir = rootDir;
	}

	private boolean _ifMathch(String srcPath, String classPath) {
		String tempClassValue = classPath.replace(
			_CLASS_PARAMETER, File.separator);

		tempClassValue = tempClassValue.substring(
			0, tempClassValue.lastIndexOf("."));

		if (srcPath.contains(_MODULE_SRC_PARAMETER)) {
			String tempSrcValue = srcPath.replace(
				_MODULE_SRC_PARAMETER, File.separator);

			tempSrcValue = tempSrcValue.substring(
				0, tempSrcValue.lastIndexOf("."));

			if (tempSrcValue.equals(tempClassValue)) {
				return true;
			}
			else {
				return false;
			}
		}
		else if (srcPath.contains(_SRC_PARAMETER)) {
			String tempSrcValue = srcPath.replace(
				_SRC_PARAMETER, File.separator);

			tempSrcValue = tempSrcValue.substring(
				0, tempSrcValue.lastIndexOf("."));

			if (tempSrcValue.equals(tempClassValue)) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}

	private boolean _ifSkipDirectory(String fileName) {
		if (_SKIP_LIST.contains(fileName) || fileName.contains(".")) {
			return true;
		}
		else {
			return false;
		}
	}

	private static final String _CLASS_PARAMETER =
		Paths.get(File.separator, "classes").toString() + File.separator;

	private static final Logger _LOGGER = Logger.getLogger(
		GetPathTask.class.getName());

	private static final String _MODULE_SRC_PARAMETER = Paths.get(
		File.separator, "src", File.separator, "main", File.separator,
		"java").toString() + File.separator;

	private static final List _SKIP_LIST = Arrays.asList(
		"node_modules", "benchmarks", "definitions", "gradle", "lib",
		"nbproject", "oss-licenses", "portal-client", "readme", "sql", "tools");

	private static final String _SRC_PARAMETER =
		Paths.get(File.separator, "src").toString() + File.separator;

	private String _classNames;
	private String _filePaths;
	private String _rootDir;

}