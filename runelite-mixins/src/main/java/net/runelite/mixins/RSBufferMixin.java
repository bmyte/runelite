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
	private static BigInteger mod = new BigInteger("a79020d1617f0ba919c482c16d3ca354b6f489c1fc0f8b7ae13f0b045d400a01909941833566aa78af9725aa374a64c5cab6a9b920c6d77d02e38c969a27c969e05a0b90b46b8bf8d6497f1f5128a7d2007dacc592ac40a393cffb7ed0283be246faabbb3d7b98bc6663678643cd33410b37d2dae42239bf606084415ee4fc97", 16);

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