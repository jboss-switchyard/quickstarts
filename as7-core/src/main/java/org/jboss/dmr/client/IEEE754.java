/*
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
*/
package org.jboss.dmr.client;

import com.google.gwt.core.client.JsArrayInteger;

public class IEEE754 {

    public static native JsArrayInteger fromFloat(float v)/*-{
        var ebits = 8;
        var fbits = 23;
        var bias = (1 << (ebits - 1)) - 1;

        // Compute sign, exponent, fraction
        var s, e, f;
        if (isNaN(v)) {
            e = (1 << bias) - 1; f = 1; s = 0;
        }
        else if (v === Infinity || v === -Infinity) {
            e = (1 << bias) - 1; f = 0; s = (v < 0) ? 1 : 0;
        }
        else if (v === 0) {
            e = 0; f = 0; s = (1 / v === -Infinity) ? 1 : 0;
        }
        else {
            s = v < 0;
            v = Math.abs(v);

            if (v >= Math.pow(2, 1 - bias)) {
                var ln = Math.min(Math.floor(Math.log(v) / Math.LN2), bias);
                e = ln + bias;
                f = v * Math.pow(2, fbits - ln) - Math.pow(2, fbits);
            }
            else {
                e = 0;
                f = v / Math.pow(2, 1 - bias - fbits);
            }
        }

        // Pack sign, exponent, fraction
        var i, bits = [];
        for (i = fbits; i; i -= 1) { bits.push(f % 2 ? 1 : 0); f = Math.floor(f / 2); }
        for (i = ebits; i; i -= 1) { bits.push(e % 2 ? 1 : 0); e = Math.floor(e / 2); }
        bits.push(s ? 1 : 0);
        bits.reverse();
        var str = bits.join('');

        // Bits to bytes
        var bytes = [];
        while (str.length) {
            bytes.push(parseInt(str.substring(0, 8), 2));
            str = str.substring(8);
        }
        return bytes;
    }-*/;

    public static native float toFloat(byte b1, byte b2, byte b3, byte b4)/*-{
        var ebits = 8;
        var fbits = 23;
        var bytes = arguments;

        // Bytes to bits
        var bits = [];
        for (var i = bytes.length; i; i -= 1) {
            var byteA = bytes[i - 1];
            for (var j = 8; j; j -= 1) {
                bits.push(byteA % 2 ? 1 : 0); byteA = byteA >> 1;
            }
        }
        bits.reverse();
        var str = bits.join('');

        // Unpack sign, exponent, fraction
        var bias = (1 << (ebits - 1)) - 1;
        var s = parseInt(str.substring(0, 1), 2) ? -1 : 1;
        var e = parseInt(str.substring(1, 1 + ebits), 2);
        var f = parseInt(str.substring(1 + ebits), 2);

        // Produce number
        if (e === (1 << ebits) - 1) {
            return f !== 0 ? NaN : s * Infinity;
        }
        else if (e > 0) {
            return s * Math.pow(2, e - bias) * (1 + f / Math.pow(2, fbits));
        }
        else if (f !== 0) {
            return s * Math.pow(2, -(bias-1)) * (f / Math.pow(2, fbits));
        }
        else {
            return s * 0;
        }
    }-*/;

    public static native JsArrayInteger fromDouble(double v)/*-{
        var ebits = 11;
        var fbits = 52;
        var bias = (1 << (ebits - 1)) - 1;

        // Compute sign, exponent, fraction
        var s, e, f;
        if (isNaN(v)) {
            e = (1 << bias) - 1; f = 1; s = 0;
        }
        else if (v === Infinity || v === -Infinity) {
            e = (1 << bias) - 1; f = 0; s = (v < 0) ? 1 : 0;
        }
        else if (v === 0) {
            e = 0; f = 0; s = (1 / v === -Infinity) ? 1 : 0;
        }
        else {
            s = v < 0;
            v = Math.abs(v);

            if (v >= Math.pow(2, 1 - bias)) {
                var ln = Math.min(Math.floor(Math.log(v) / Math.LN2), bias);
                e = ln + bias;
                f = v * Math.pow(2, fbits - ln) - Math.pow(2, fbits);
            }
            else {
                e = 0;
                f = v / Math.pow(2, 1 - bias - fbits);
            }
        }

        // Pack sign, exponent, fraction
        var i, bits = [];
        for (i = fbits; i; i -= 1) { bits.push(f % 2 ? 1 : 0); f = Math.floor(f / 2); }
        for (i = ebits; i; i -= 1) { bits.push(e % 2 ? 1 : 0); e = Math.floor(e / 2); }
        bits.push(s ? 1 : 0);
        bits.reverse();
        var str = bits.join('');

        // Bits to bytes
        var bytes = [];
        while (str.length) {
            bytes.push(parseInt(str.substring(0, 8), 2));
            str = str.substring(8);
        }
        return bytes;
    }-*/;

    public static native double toDouble(byte[] bytes) /*-{
        var ebits = 11;
        var fbits = 52;

        // Bytes to bits
        var bits = [];
        for (var i = bytes.length; i; i -= 1) {
            var byteA = bytes[i - 1];
            for (var j = 8; j; j -= 1) {
                bits.push(byteA % 2 ? 1 : 0); byteA = byteA >> 1;
            }
        }
        bits.reverse();
        var str = bits.join('');

        // Unpack sign, exponent, fraction
        var bias = (1 << (ebits - 1)) - 1;
        var s = parseInt(str.substring(0, 1), 2) ? -1 : 1;
        var e = parseInt(str.substring(1, 1 + ebits), 2);
        var f = parseInt(str.substring(1 + ebits), 2);

        // Produce number
        if (e === (1 << ebits) - 1) {
            return f !== 0 ? NaN : s * Infinity;
        }
        else if (e > 0) {
            return s * Math.pow(2, e - bias) * (1 + f / Math.pow(2, fbits));
        }
        else if (f !== 0) {
            return s * Math.pow(2, -(bias-1)) * (f / Math.pow(2, fbits));
        }
        else {
            return s * 0;
        }
    }-*/;

//    function fromIEEE754Double(b) { return fromIEEE754(b, 11, 52); }
//    function   toIEEE754Double(v) { return   toIEEE754(v, 11, 52); }
//    function fromIEEE754Single(b) { return fromIEEE754(b,  8, 23); }
//    function   toIEEE754Single(v) { return   toIEEE754(v,  8, 23); }

}
