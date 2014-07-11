/**
 * Copyright (c) 2014 Lewis Chun
 * Android game in which the user controls a ball to get a high score.
 *
 * This file is part of Roll Ball Roll.
 *
 * Roll Ball Roll is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.zwac035.finalprogram.box;

import com.zwac035.finalprogram.Res;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Lewis Chun
 */
public enum BoxType {
    PLAIN,
    EXPLOSION,
    MAGNET,
    HEAVY;
    
    // Taken from:
    // http://stackoverflow.com/questions/1972392/java-pick-a-random-value-from-an-enum
    private static final List<BoxType> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    
    public static BoxType random() {
        return VALUES.get(Res.rnd.nextInt(SIZE));
    }
}
