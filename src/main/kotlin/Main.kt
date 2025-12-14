import java.util.Scanner

data class Archive(val name: String, val notes: MutableList<Note> = mutableListOf())
data class Note(val title: String, val content: String)

open class Menu<T>(private val title: String) {
    protected val scanner = Scanner(System.`in`)
    protected val items = mutableListOf<MenuItem<T>>()

    data class MenuItem<T>(val name: String, val action: () -> T)

    fun show(): T {
        while (true) {
            println("\n$title")
            items.forEachIndexed { index, item ->
                println("$index. ${item.name}")
            }

            print("Выберите пункт меню: ")
            val input = scanner.nextLine()

            try {
                val choice = input.toInt()
                if (choice in items.indices) {
                    return items[choice].action()
                } else {
                    println("Ошибка: пункт $choice не существует. Пожалуйста, выберите из списка.")
                }
            } catch (e: NumberFormatException) {
                println("Ошибка: пожалуйста, введите число.")
            }
        }
    }

    protected fun readNonEmptyString(prompt: String): String {
        while (true) {
            print(prompt)
            val input = scanner.nextLine().trim()
            if (input.isNotEmpty()) {
                return input
            }
            println("Ошибка: значение не может быть пустым. Попробуйте еще раз.")
        }
    }
}

class NoteMenu(private val archive: Archive) : Menu<Unit>("Архив: ${archive.name}") {

    init {
        updateMenuItems()
    }

    private fun updateMenuItems() {
        items.clear()

        items.add(MenuItem("Создать заметку") {
            createNote()
            updateMenuItems()
            Unit
        })

        archive.notes.forEach { note ->
            items.add(MenuItem(note.title) {
                viewNote(note)
                updateMenuItems()
                Unit
            })
        }

        items.add(MenuItem("Назад") {
            Unit
        })
    }

    private fun createNote() {
        val title = readNonEmptyString("Введите заголовок заметки: ")
        val content = readNonEmptyString("Введите текст заметки: ")
        archive.notes.add(Note(title, content))
        println("Заметка '$title' успешно создана!")
    }

    private fun viewNote(note: Note) {
        println("\nЗаголовок: ${note.title}")
        println("Текст заметки:")
        println(note.content)
        print("\nНажмите Enter для возврата...")
        scanner.nextLine()
    }
}

class ArchiveMenu(private val archives: MutableList<Archive>) : Menu<Unit>("Список архивов") {

    init {
        updateMenuItems()
    }

    private fun updateMenuItems() {
        items.clear()

        items.add(MenuItem("Создать архив") {
            createArchive()
            updateMenuItems()
            Unit
        })

        archives.forEach { archive ->
            items.add(MenuItem(archive.name) {
                NoteMenu(archive).show()
                updateMenuItems()
                Unit
            })
        }

        items.add(MenuItem("Выход") {
            println("До свидания!")
            kotlin.system.exitProcess(0)
        })
    }

    private fun createArchive() {
        val name = readNonEmptyString("Введите имя архива: ")
        archives.add(Archive(name))
        println("Архив '$name' успешно создан!")
    }

    fun start() {
        while (true) {
            show()
        }
    }
}

fun main() {
    println("Приложение Заметки")

    val archives = mutableListOf<Archive>()
    val archiveMenu = ArchiveMenu(archives)

    archiveMenu.start()
}