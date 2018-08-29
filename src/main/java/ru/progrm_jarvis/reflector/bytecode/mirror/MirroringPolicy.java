/*
 *  Copyright 2018 Petr P.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ru.progrm_jarvis.reflector.bytecode.mirror;

public enum MirroringPolicy {

    /**
     * No members should be mirrored
     */
    NONE,
    /**
     * Only visible methods (<i>public</i>) should be mirrored
     */
    VISIBLE,
    /**
     * Only members related to implementing some interfaces should be mirrored
     */
    IMPLEMENTING,
    /**
     * Only annotated members should be mirrored
     */
    ANNOTATED,
    /**
     * Each member should be mirrored
     */
    ALL
}
