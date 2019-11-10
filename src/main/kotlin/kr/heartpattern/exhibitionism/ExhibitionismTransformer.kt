package kr.heartpattern.exhibitionism

import org.objectweb.asm.*

class ExhibitionismTransformer(cv: ClassVisitor, private val option: ExhibitionismOptions) :
    ClassVisitor(Opcodes.ASM7, cv) {
    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        return super.visitMethod(transformAccess(access), name, descriptor, signature, exceptions)
    }

    override fun visitModule(name: String?, access: Int, version: String?): ModuleVisitor {
        return super.visitModule(name, transformAccess(access), version)
    }

    override fun visitInnerClass(name: String?, outerName: String?, innerName: String?, access: Int) {
        return super.visitInnerClass(name, outerName, innerName, transformAccess(access))
    }

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        return super.visit(version, transformAccess(access), name, signature, superName, interfaces)
    }

    override fun visitField(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor {
        return super.visitField(transformAccess(access), name, descriptor, signature, value)
    }

    private fun transformAccess(value: Int): Int {
        var access = value
        if (option.public) {
            access = (access and (Opcodes.ACC_PRIVATE or Opcodes.ACC_PROTECTED).inv()) or Opcodes.ACC_PUBLIC
        }
        if (option.open) {
            access = access and Opcodes.ACC_FINAL.inv()
        }
        return access
    }
}