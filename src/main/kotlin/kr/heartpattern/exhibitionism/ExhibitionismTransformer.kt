package kr.heartpattern.exhibitionism

import org.objectweb.asm.*
import org.objectweb.asm.Opcodes.*

class ExhibitionismTransformer(cv: ClassVisitor, private val option: ExhibitionismOptions) :
    ClassVisitor(Opcodes.ASM9, cv) {
    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        return super.visitMethod(transformAccess(Type.METHOD, access), name, descriptor, signature, exceptions)
    }

    override fun visitModule(name: String?, access: Int, version: String?): ModuleVisitor {
        return super.visitModule(name, transformAccess(Type.MODULE, access), version)
    }

    override fun visitInnerClass(name: String?, outerName: String?, innerName: String?, access: Int) {
        return super.visitInnerClass(name, outerName, innerName, transformAccess(Type.CLASS, access))
    }

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        return super.visit(version, transformAccess(Type.CLASS, access), name, signature, superName, interfaces)
    }

    override fun visitField(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor {
        return super.visitField(transformAccess(Type.FIELD, access), name, descriptor, signature, value)
    }

    private fun transformAccess(type: Type, value: Int): Int {
        var access = value
        if (option.public) {
            access = (access and (ACC_PRIVATE or ACC_PROTECTED).inv()) or ACC_PUBLIC
        }
        if (option.open && (type != Type.FIELD || !option.noStaticFinal || access and (ACC_FINAL and ACC_STATIC) != (ACC_FINAL and ACC_STATIC))) {
            access = access and ACC_FINAL.inv()
        }
        return access
    }

    private enum class Type {
        MODULE, CLASS, FIELD, METHOD
    }
}
