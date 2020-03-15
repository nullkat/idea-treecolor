/*
 * Copyright (c) 2018-2020 Pavel Barykin.
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

package dev.pnbarx.idea.treecolor.state;

import dev.pnbarx.idea.treecolor.state.models.HighlightedFile;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class FilesState {

    private static final Logger LOG = Logger.getInstance(FilesState.class);

    public List<HighlightedFile> state;
    private ProjectState projectState;

    FilesState(ProjectState projectState) {
        this.projectState = projectState;
    }

    public void linkState(List<HighlightedFile> state) {
        this.state = state;
    }

    @Nullable
    public Integer getNodeColorIndex(VirtualFile file) {
        Integer colorIndex = null;
        String path = file.getPath() + "/";

        for (HighlightedFile node : state) {
            if (path.startsWith(node.path + "/")) {
                colorIndex = node.colorIndex - 1;
            }
        }

        return colorIndex;
    }

    public boolean isHighlighted(VirtualFile[] files, boolean recursive) {
        if (recursive) {
            return Arrays.stream(files).anyMatch(file ->
                    state.stream().anyMatch(node ->
                            (node.path).startsWith(file.getPath() + "/")));
        } else {
            return Arrays.stream(files).anyMatch(file ->
                    state.stream().anyMatch(node ->
                            (node.path).equals(file.getPath())));
        }
    }

    public void addNode(VirtualFile file, int colorIndex) {
        String path = file.getPath();
        state.removeIf(node -> node.path.equals(path));
        state.add(new HighlightedFile(path, colorIndex + 1));
        state.sort(Comparator.comparing(node -> node.path));
        projectState.updateUI();
    }

    public void addNodes(VirtualFile[] files, int colorIndex) {
        for (VirtualFile file : files) {
            addNode(file, colorIndex);
        }
    }

    public void removeNode(VirtualFile file, boolean recursive) {
        String path = file.getPath();
        if (recursive) {
            state.removeIf(node -> (node.path + "/").startsWith(path + "/"));
        } else {
            state.removeIf(node -> node.path.equals(path));
        }
        projectState.updateUI();
    }

    public void removeNodes(VirtualFile[] files, boolean recursive) {
        for (VirtualFile file : files) {
            removeNode(file, recursive);
        }
    }

}
