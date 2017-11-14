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

import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.File;
import java.io.IOException;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
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

		String[] classNames = StringUtil.split(_classNames);

		StringBundler classPropertyValue = new StringBundler();

		List<String> classFiles = new ArrayList<>();

		List classResultList = new ArrayList();

		for (String className : classNames) {
			classFiles.add(className + ".class");
		}

		findFiles(baseDIR, classFiles, classResultList);

		if (classResultList.isEmpty()) {
			_LOGGER.log(
				Level.WARNING, "{0} does not exist!", classNames);
		}

		classResultList.removeAll(classResultList);

		if (!classResultList.isEmpty()) {
			for (int i = 0; i < classResultList.size(); i++) {
				classPropertyValue.append(
					String.valueOf(classResultList.get(i)));
				classPropertyValue.append(",");
			}

			classPropertyValue.setIndex(classPropertyValue.index() - 1);

			getProject().setProperty(
				_classFiles, classPropertyValue.toString());
		}

		if (_includeSrcFile) {
			List<String> srcFiles = new ArrayList<>();

			for (String className : classNames) {
				srcFiles.add(_srcFileName + ".java");
			}

			List srcResultList = new ArrayList();

			StringBundler srcPropertyValue = new StringBundler();

			findFiles(baseDIR, srcFiles, srcResultList);

			if (srcResultList.isEmpty()) {
				_LOGGER.log(Level.WARNING, "Java File does not exist!");
			}
			else {
				for (int i = 0; i < srcResultList.size(); i++) {
					String srcPath = String.valueOf(srcResultList.get(i));

					if (_ifMathch(srcPath, _absolutePathClassFile)) {
						int endIndex = srcPath.indexOf("src");

						srcPropertyValue.append(srcPath.substring(0, endIndex));
					}
				}

				getProject().setProperty(
					_srcFileBaseDir, srcPropertyValue.toString());
			}
		}
	}

	public void findFiles(
		String baseDirPath, List<String> classFileNames, List<File> fileList) {

		String tempName = null;

		File baseDir = new File(baseDirPath);

		if (!baseDir.exists() || !baseDir.isDirectory()) {
			return;
		}

		String[] baseDirfileList = baseDir.list();

		try {
			Files.walkFileTree(
				baseDir.toPath(),
				new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult visitFile(
							Path file, BasicFileAttributes attrs)
						throws IOException {

						Path fileNamePath = file.getFileName();

						String fileName = fileNamePath.toString();

						if (classFileNames.contains(fileName)) {
							fileList.add(file.toFile());
						}

						return FileVisitResult.CONTINUE;
					}

				});
		}
		catch (IOException ioe) {
			throw new BuildException(ioe);
		}
	}

	public void setAbsolutePathClassFile(String absolutePathClassFile) {
		_absolutePathClassFile = absolutePathClassFile;
	}

	public void setClassFiles(String classFiles) {
		_classFiles = classFiles;
	}

	public void setclassNames(String classNames) {
		_classNames = classNames;
	}

	public void setIncludeSrcFile(boolean includeSrcFile) {
		_includeSrcFile = includeSrcFile;
	}

	public void setRootDir(String rootDir) {
		_rootDir = rootDir;
	}

	public void setSrcFileBaseDir(String srcFilesBaseDir) {
		_srcFileBaseDir = srcFilesBaseDir;
	}

	public void setSrcFileName(String srcFileName) {
		_srcFileName = srcFileName;
	}

	private boolean _ifMathch(String srcPath, String classPath) {
		String tempClassValue = classPath.replace(
			_CLASS_PARAMETER, File.separator);

		tempClassValue = tempClassValue.substring(
			0, tempClassValue.lastIndexOf("."));

		if (srcPath.contains(_SRC_PARAMETER)) {
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
		else if (srcPath.contains(_MODULE_SRC_PARAMETER)) {
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
		else {
			return false;
		}
	}

	private static final String _CLASS_PARAMETER =
		File.separator + "classes" + File.separator;

	private static final Logger _LOGGER = Logger.getLogger(
		GetPathTask.class.getName());

	private static final String _MODULE_SRC_PARAMETER =
		File.separator + "src" + File.separator + "main" + File.separator +
			"java" + File.separator;

	private static final String _SRC_PARAMETER =
		File.separator + "src" + File.separator;

	private String _absolutePathClassFile;
	private String _classFiles;
	private String _classNames;
	private boolean _includeSrcFile;
	private String _rootDir;
	private String _srcFileBaseDir;
	private String _srcFileName;

}