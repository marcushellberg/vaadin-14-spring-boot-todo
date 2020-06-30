package org.vaadin.marcus;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Route("")
@Theme(value = Lumo.class, variant = Lumo.DARK)
public class TodoView extends VerticalLayout {

    private TodoRepo repo;
    TextField taskField = new TextField();
    Button addButton = new Button("Add");
    VerticalLayout todosList = new VerticalLayout();
    Button clearButton = new Button("Clear completed");

    public TodoView(TodoRepo repo) {
        this.repo = repo;

        add(
            new H1("Important stuff:"),
            new HorizontalLayout(taskField, addButton),
            todosList,
            clearButton
        );


        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickShortcut(Key.ENTER);
        addButton.addClickListener(e -> {
            repo.save(new Todo(taskField.getValue()));
            taskField.clear();
            taskField.focus();

            refreshTodos();
        });

        clearButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        clearButton.addClickListener(e->{
           repo.deleteByDone(true);
           refreshTodos();
        });

        refreshTodos();
    }

    private void refreshTodos() {
        todosList.removeAll();

        repo.findAll()
            .stream()
            .map(TodoLayout::new)
            .forEach(todosList::add);
    }

    class TodoLayout extends HorizontalLayout {
        Checkbox done = new Checkbox();
        TextField task = new TextField();

        public TodoLayout(Todo todo) {
            add(done, task);
            setDefaultVerticalComponentAlignment(Alignment.CENTER);

            Binder<Todo> binder = new Binder<>(Todo.class);
            binder.bindInstanceFields(this);
            binder.setBean(todo);

            binder.addValueChangeListener(e -> {
                repo.save(binder.getBean());
            });
        }
    }
}
