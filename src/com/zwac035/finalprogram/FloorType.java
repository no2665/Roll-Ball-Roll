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
package com.zwac035.finalprogram;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * An enum for the different floor pieces.
 * @author Lewis Chun
 */
public enum FloorType {
    /**
     * No hole
     */
    PLAIN,
    /**
     * Hole in the centre
     */
    CENTRE_HOLE,
    /**
     * Hole at 1 along the x axis
     */
    X1_HOLE, 
    /**
     * Hole at 2 along the x axis
     */
    X2_HOLE,
    X3_HOLE,
    X4_HOLE,
    /**
     * Hole at -1 along the x axis
     */
    MIN_X1_HOLE, 
    MIN_X2_HOLE,
    MIN_X3_HOLE,
    MIN_X4_HOLE;
    
    // Taken from:
    // http://stackoverflow.com/questions/1972392/java-pick-a-random-value-from-an-enum
    private static final List<FloorType> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    
    /**
     * @return A random value from this enum
     */
    public static FloorType random() {
        return VALUES.get(Res.rnd.nextInt(SIZE));
    }
}
