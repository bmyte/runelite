/*
 * Copyright (c) 2019, Null (zeruth)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.mixins;

import java.math.BigInteger;
import net.runelite.api.mixins.Copy;
import net.runelite.api.mixins.Inject;
import net.runelite.api.mixins.Mixin;
import net.runelite.api.mixins.Shadow;
import net.runelite.api.mixins.Replace;
import net.runelite.rs.api.RSBuffer;

@Mixin(RSBuffer.class)
public abstract class RSBufferMixin implements RSBuffer
{
	@Inject
	private static BigInteger exp = new BigInteger("10001", 16);

	@Shadow("modulus")
	private static BigInteger mod = new BigInteger("db1dc3067a0e86b4ab98b9ac9b5b63c8c4981b479a87444e15b890655ad1aed0c16d9e923d9f8742cb779d26d01e9fc0335277a63f3d5d290b3197201f8da3a906a88e147cb818ce1712f76ba6cdd796c3802e14a2c7f7834391703c81e923733301425d60a1a972e72806439d82d77cf3cc274125e7046d41a25e2376cffb8f", 16);

	@Copy("encryptRsa")
	public void rs$encryptRsa(BigInteger var1, BigInteger var2)
	{
	}

	@Replace("encryptRsa")
	public void rl$encryptRsa(BigInteger var1, BigInteger var2)
	{
		rs$encryptRsa(exp, mod);
	}
}