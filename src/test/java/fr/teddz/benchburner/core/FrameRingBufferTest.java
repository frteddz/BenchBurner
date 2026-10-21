package fr.teddz.benchburner.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FrameRingBufferTest {
@Test
void wrapsAroundAndKeepsNewestValues() {
FrameRingBuffer ring = new FrameRingBuffer(3);
ring.add(1);
ring.add(2);
ring.add(3);
ring.add(4);
ring.add(5);
double[] out = new double[3];
ring.copyInto(out);
assertEquals(3, ring.count());
assertEquals(3, out[0]);
assertEquals(4, out[1]);
assertEquals(5, out[2]);
}

@Test
void ignoresInvalidValues() {
FrameRingBuffer ring = new FrameRingBuffer(4);
ring.add(Double.NaN);
ring.add(0);
ring.add(-5);
ring.add(16);
assertEquals(1, ring.count());
}

@Test
void copyLatestReturnsNewestWindow() {
FrameRingBuffer ring = new FrameRingBuffer(5);
for (int i = 1; i <= 8; i++) ring.add(i);
double[] out = new double[3];
int copied = ring.copyLatest(out);
assertEquals(3, copied);
assertEquals(6, out[0]);
assertEquals(7, out[1]);
assertEquals(8, out[2]);
}

@Test
void tinyCapacityStillWorks() {
FrameRingBuffer ring = new FrameRingBuffer(0);
assertEquals(1, ring.capacity());
ring.add(10);
assertEquals(1, ring.count());
double[] out = new double[1];
ring.copyInto(out);
assertEquals(10, out[0]);
}

@Test
void copyIntoFillsInOrderAfterManyAdds() {
FrameRingBuffer ring = new FrameRingBuffer(4);
for (int i = 1; i <= 10; i++) ring.add(i);
double[] out = new double[4];
ring.copyInto(out);
assertEquals(7, out[0], 0.0001);
assertEquals(8, out[1], 0.0001);
assertEquals(9, out[2], 0.0001);
assertEquals(10, out[3], 0.0001);
assertTrue(ring.count() == 4);
}
}
